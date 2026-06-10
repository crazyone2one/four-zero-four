package cn.master.system.service;

import cn.master.security.util.SecurityUtils;
import cn.master.system.entity.ProjectParameter;
import cn.master.system.mapper.ProjectParameterMapper;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

import static cn.master.system.entity.table.ProjectParameterTableDef.PROJECT_PARAMETER;

/**
 * @author : 11's papa
 * @since : 2026/6/10, 星期三
 **/
@Component
@RequiredArgsConstructor
public class CreateParamsResourceService implements CreateProjectResourceService {
    private final ProjectParameterMapper projectParameterMapper;

    @Override
    public void createDataSourceResources(String projectId) {
        boolean exists = QueryChain.of(ProjectParameter.class).where(PROJECT_PARAMETER.PROJECT_ID.eq(projectId)
                        .and(PROJECT_PARAMETER.PARAM_TYPE.eq("datasource")))
                .exists();
        if (!exists) {
            Map<String, Object> dataSource = Map.of("dataSource", "", "dbUrl", "", "username", "", "password", "");
            ProjectParameter dsParameter = ProjectParameter.builder()
                    .projectId(projectId)
                    .paramType("datasource")
                    .parameters(dataSource)
                    .createUser(SecurityUtils.getUserId())
                    .updateUser(SecurityUtils.getUserId())
                    .build();
            projectParameterMapper.insert(dsParameter);
        }
    }

    @Override
    public void createHostResources(String projectId) {
        boolean exists = QueryChain.of(ProjectParameter.class).where(PROJECT_PARAMETER.PROJECT_ID.eq(projectId)
                        .and(PROJECT_PARAMETER.PARAM_TYPE.eq("host")))
                .exists();
        if (!exists) {
            Map<String, Object> dataSource = Map.of("host", "", "port", "", "username", "",
                    "password", "", "remotePath", "", "localPath", "");
            ProjectParameter dsParameter = ProjectParameter.builder()
                    .projectId(projectId)
                    .paramType("host")
                    .parameters(dataSource)
                    .createUser(SecurityUtils.getUserId())
                    .updateUser(SecurityUtils.getUserId())
                    .build();
            projectParameterMapper.insert(dsParameter);
        }
    }
}
