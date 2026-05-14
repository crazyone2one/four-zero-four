package cn.master.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.system.entity.Project;
import cn.master.system.mapper.ProjectMapper;
import cn.master.system.service.ProjectService;
import org.springframework.stereotype.Service;

/**
 * 项目 服务层实现。
 *
 * @author 11's papa
 * @since 2026-05-13
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project>  implements ProjectService{

}
