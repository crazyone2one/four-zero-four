package cn.master.system.service;

/**
 * @author : 11's papa
 * @since : 2026/6/10, 星期三
 **/
public interface CreateProjectResourceService {
    void createDataSourceResources(String projectId);

    void createHostResources(String projectId);
}
