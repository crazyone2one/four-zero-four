package cn.master.system.service.impl;

import cn.master.dto.BasePageRequest;
import cn.master.dto.BatchProcessDTO;
import cn.master.dto.BatchProcessResponse;
import cn.master.exception.FZFException;
import cn.master.result.SystemResultCode;
import cn.master.system.dto.user.*;
import cn.master.system.entity.SystemUser;
import cn.master.system.entity.UserRoleRelation;
import cn.master.system.log.service.UserLogService;
import cn.master.system.mapper.SystemUserMapper;
import cn.master.system.service.OperationLogService;
import cn.master.system.service.SystemUserService;
import cn.master.system.service.UserRoleRelationService;
import cn.master.system.service.UserRoleService;
import cn.master.util.JsonUtils;
import cn.master.util.StringUtils;
import cn.master.util.Translator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static cn.master.system.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.system.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;

/**
 * 用户 服务层实现。
 *
 * @author 11's papa
 * @since 2026-05-12
 */
@Service
@RequiredArgsConstructor
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRelationService userRoleRelationService;
    private final OperationLogService operationLogService;
    private final UserLogService userLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserBatchCreateResponse addUser(UserBatchCreateRequest userCreateDTO, String operator) {
        userRoleService.checkRoleIsGlobalAndHaveMember(userCreateDTO.userRoleIdList(), true);
        UserBatchCreateResponse response = new UserBatchCreateResponse();
        Map<String, String> errorEmails = validateUserInfo(userCreateDTO.userInfoList().stream().map(UserCreateInfo::getEmail).toList());
        if (MapUtils.isNotEmpty(errorEmails)) {
            response.setErrorEmails(errorEmails);
            throw new FZFException(SystemResultCode.INVITE_EMAIL_EXIST, JsonUtils.toJSONString(errorEmails.keySet()));
        } else {
            response.setSuccessList(saveUserAndRole(userCreateDTO, operator, "/system/user/addUser"));
        }
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchProcessResponse updateUserEnable(UserChangeEnableRequest request, String operatorId, String operatorName) {
        if (request.isSelectAll()) {
            List<String> userIdList = new ArrayList<>(queryChain().where(SYSTEM_USER.EMAIL.like(request.getCondition().getKeyword())
                            .or(SYSTEM_USER.NAME.like(request.getCondition().getKeyword()))).list()
                    .stream().map(SystemUser::getId).toList());
            if (!request.getExcludeIds().isEmpty()) {
                userIdList.removeAll(request.getExcludeIds());
            }
            request.setSelectIds(userIdList);
        }
        checkUserInDb(request.getSelectIds());
        if (!request.isEnable()) {
            // 不能禁用当前用户和admin
            checkProcessUserAndThrowException(request.getSelectIds(), operatorId, operatorName, Translator.get("user.not.disable"));
        }
        request.getSelectIds().forEach(userId -> updateChain().where(SYSTEM_USER.ID.eq(userId))
                .set(SYSTEM_USER.ENABLE, request.isEnable())
                .set(SYSTEM_USER.UPDATE_USER, operatorName)
                .update());
        BatchProcessResponse response = new BatchProcessResponse();
        response.setTotalCount(request.getSelectIds().size());
        response.setSuccessCount(request.getSelectIds().size());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchProcessResponse deleteUser(BatchProcessDTO request, String operatorId, String operatorName) {
        List<String> userIdList = new ArrayList<>(queryChain().where(SYSTEM_USER.EMAIL.like(request.getCondition().getKeyword())
                        .or(SYSTEM_USER.NAME.like(request.getCondition().getKeyword()))).list()
                .stream().map(SystemUser::getId).toList());
        checkUserInDb(userIdList);
        checkProcessUserAndThrowException(userIdList, operatorId, operatorName, Translator.get("user.not.delete"));
        mapper.deleteBatchByIds(userIdList);
        userRoleRelationService.deleteByUserIdList(userIdList);
        BatchProcessResponse response = new BatchProcessResponse();
        response.setTotalCount(userIdList.size());
        response.setSuccessCount(userIdList.size());
        return response;
    }

    @Override
    public UserEditRequest updateUser(UserEditRequest request, String operatorId) {
        userRoleService.checkRoleIsGlobalAndHaveMember(request.getUserRoleIdList(), true);
        checkUserEmail(request.getId(), request.getEmail());
        SystemUser user = new SystemUser();
        BeanUtils.copyProperties(request, user);
        user.setUpdateUser(operatorId);
        mapper.update(user);
        userRoleRelationService.updateUserSystemGlobalRole(user, user.getUpdateUser(), request.getUserRoleIdList());
        return request;
    }

    @Override
    public Page<UserTableVO> getUserPage(BasePageRequest request) {
        Page<UserTableVO> page = queryChain()
                .where(SYSTEM_USER.EMAIL.like(request.getKeyword())
                        .or(SYSTEM_USER.NAME.like(request.getKeyword()))
                        .or(SYSTEM_USER.ID.eq(request.getKeyword()))
                        .or(SYSTEM_USER.PHONE.eq(request.getKeyword())))
                .orderBy(SYSTEM_USER.CREATE_TIME.desc(), SYSTEM_USER.ID.desc())
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), UserTableVO.class);
        if (page.getTotalPage() > 0) {
            List<UserTableVO> userList = page.getRecords();
            List<String> userIdList = userList.stream().map(UserTableVO::getId).toList();
            Map<String, UserTableVO> roleAndOrganizationMap = userRoleRelationService.selectGlobalUserRoleAndOrganization(userIdList);
            for (UserTableVO user : userList) {
                UserTableVO roleOrgModel = roleAndOrganizationMap.get(user.getId());
                if (roleOrgModel != null) {
                    user.setUserRoleList(roleOrgModel.getUserRoleList());
                }
            }
        }
        return page;
    }

    @Override
    public UserDTO getUserDTOByKeyword(String keyword) {
        UserDTO userDTO = queryChain().where(SYSTEM_USER.EMAIL.like(keyword)
                .or(SYSTEM_USER.NAME.like(keyword))
                .or(SYSTEM_USER.ID.like(keyword))
                .or(SYSTEM_USER.PHONE.like(keyword))).oneAs(UserDTO.class);
        if (userDTO != null) {
            userDTO.setUserRoleRelations(QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.eq(userDTO.getId())).list());
            userDTO.setUserRoles(
                    userRoleService.selectByUserRoleRelations(userDTO.getUserRoleRelations())
            );
        }
        return userDTO;
    }

    private void checkUserEmail(String id, String email) {
        if (queryChain().where(SYSTEM_USER.ID.ne(id).and(SYSTEM_USER.EMAIL.eq(email))).count() > 0) {
            throw new FZFException(Translator.get("user_email_already_exists"));
        }
    }

    private void checkProcessUserAndThrowException(@Valid List<String> userIdList, String operatorId, String operatorName, String exceptionMessage) {
        for (String userId : userIdList) {
            // 当前用户或admin不能被操作
            if (StringUtils.equals(userId, operatorId)) {
                throw new FZFException(exceptionMessage + ":" + operatorName);
            } else if (StringUtils.equals(userId, "admin")) {
                throw new FZFException(exceptionMessage + ": admin");
            }
        }
    }

    private void checkUserInDb(@Valid List<String> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            throw new FZFException(Translator.get("user.not.exist"));
        }
        List<SystemUser> userInDb = queryChain().where(SYSTEM_USER.ID.in(userIdList)).list();
        if (userIdList.size() != userInDb.size()) {
            throw new FZFException(Translator.get("user.not.exist"));
        }
    }

    private List<UserCreateInfo> saveUserAndRole(UserBatchCreateRequest userCreateDTO, String operator, String path) {
        saveUserAndRole(userCreateDTO, operator);
        operationLogService.batchAdd(userLogService.getBatchAddLogs(userCreateDTO.userInfoList(), operator, path));
        return userCreateDTO.userInfoList();
    }

    private void saveUserAndRole(UserBatchCreateRequest userCreateDTO, String operator) {
        List<String> userIdList = new ArrayList<>();
        userCreateDTO.userInfoList().forEach(userInfo -> {
            SystemUser user = new SystemUser();
            user.setEmail(userInfo.getEmail());
            user.setName(userInfo.getName());
            user.setPassword(passwordEncoder.encode(userInfo.getEmail()));
            user.setPhone(userInfo.getPhone());
            user.setCreateUser(operator);
            user.setUpdateUser(operator);
            user.setEnable(true);
            mapper.insertSelective(user);
            userIdList.add(user.getId());
        });
        userRoleRelationService.addUserRoleRelation(userIdList, userCreateDTO.userRoleIdList());
    }

    private Map<String, String> validateUserInfo(Collection<String> createEmails) {
        Map<String, String> errorMessage = new HashMap<>();
        String userEmailRepeatError = Translator.get("user.email.repeat");
        // 判断参数内是否含有重复邮箱
        List<String> emailList = new ArrayList<>();
        Map<String, String> userInDbMap = queryChain().select(SYSTEM_USER.EMAIL, SYSTEM_USER.ID, SYSTEM_USER.NAME)
                .where(SYSTEM_USER.EMAIL.in(createEmails)).list()
                .stream().collect(Collectors.toMap(SystemUser::getEmail, SystemUser::getName));
        for (String createEmail : createEmails) {
            if (emailList.contains(createEmail)) {
                errorMessage.put(createEmail, userEmailRepeatError);
            } else {
                if (userInDbMap.containsKey(createEmail)) {
                    errorMessage.put(createEmail, userEmailRepeatError);
                } else {
                    emailList.add(createEmail);
                }
            }
        }
        return errorMessage;
    }
}
