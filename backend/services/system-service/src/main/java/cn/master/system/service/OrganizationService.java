package cn.master.system.service;

import cn.master.system.entity.Organization;
import com.mybatisflex.core.service.IService;

/**
 * 组织 服务层。
 *
 * @author 11's papa
 * @since 2026-05-13
 */
public interface OrganizationService extends IService<Organization> {

    Organization checkResourceExist(String id);
}
