package cn.master.system.service;

import cn.master.dto.BasePageRequest;
import cn.master.dto.BatchProcessDTO;
import cn.master.dto.BatchProcessResponse;
import cn.master.system.dto.user.*;
import cn.master.system.entity.SystemUser;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

/**
 * 用户 服务层。
 *
 * @author 11's papa
 * @since 2026-05-12
 */
public interface SystemUserService extends IService<SystemUser> {
    UserBatchCreateResponse addUser(UserBatchCreateRequest userCreateDTO, String operator);

    BatchProcessResponse updateUserEnable(UserChangeEnableRequest request, String operatorId, String operatorName);

    BatchProcessResponse deleteUser(BatchProcessDTO request, String operatorId, String operatorName);

    UserEditRequest updateUser(UserEditRequest request, String operatorId);

    Page<UserTableVO> getUserPage(BasePageRequest request);

    UserDTO getUserDTOByKeyword(String keyword);
}
