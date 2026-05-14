package cn.master.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.system.entity.Organization;
import cn.master.system.mapper.OrganizationMapper;
import cn.master.system.service.OrganizationService;
import org.springframework.stereotype.Service;

/**
 * 组织 服务层实现。
 *
 * @author 11's papa
 * @since 2026-05-13
 */
@Service
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper, Organization>  implements OrganizationService{

}
