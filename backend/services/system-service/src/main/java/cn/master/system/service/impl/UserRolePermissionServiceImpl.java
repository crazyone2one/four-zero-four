package cn.master.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.system.entity.UserRolePermission;
import cn.master.system.mapper.UserRolePermissionMapper;
import cn.master.system.service.UserRolePermissionService;
import org.springframework.stereotype.Service;

/**
 * 用户组权限 服务层实现。
 *
 * @author 11's papa
 * @since 2026-05-13
 */
@Service
public class UserRolePermissionServiceImpl extends ServiceImpl<UserRolePermissionMapper, UserRolePermission>  implements UserRolePermissionService{

}
