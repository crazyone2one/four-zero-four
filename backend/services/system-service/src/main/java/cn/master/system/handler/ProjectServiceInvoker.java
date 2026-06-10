package cn.master.system.handler;

import cn.master.system.service.CreateProjectResourceService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/6/10, 星期三
 **/
@Component
public class ProjectServiceInvoker {
    private final List<CreateProjectResourceService> createProjectResourceServices;

    public ProjectServiceInvoker(List<CreateProjectResourceService> createProjectResourceServices) {
        this.createProjectResourceServices = createProjectResourceServices;
    }

    public void invokeCreateServices(String projectId) {
        for (CreateProjectResourceService service : createProjectResourceServices) {
            service.createDataSourceResources(projectId);
            service.createHostResources(projectId);
        }
    }
}
