import FZFR from "/@/api";
import type {
    IAddUserToOrgOrProjectParams,
    ICreateOrUpdateSystemProjectParams,
    IProjectOption,
    IProjectPageItem
} from "/@/typings/project.ts";
import type {IPageResponse, ITableQueryParams} from "/@/typings/common";
import type {IUserOption} from "/@/typings/user.ts";

export const projectApi = {
    getProjectList: (organizationId: string) =>
        FZFR.Get<Array<IProjectOption>>(`/system/project/list/options/${organizationId}`),
    createProject: (data: ICreateOrUpdateSystemProjectParams) =>
        FZFR.Post(data.id ? '/system/project/update' : `/system/project/save`, data),
    switchProject: (data: { projectId: string, userId: string }) =>
        FZFR.Post('/system/project/switch', data),
    // 系统-获取项目列表
    fetchProjectPage: (params: ITableQueryParams) =>
        FZFR.Post<IPageResponse<IProjectPageItem>>('/system/project/page', params),
    // 修改项目名称
    modifyProjectName: (params: {
        id: string;
        name: string;
        organizationId: string
    }) => FZFR.Post('system/project/rename', params),
    enableOrDisableProject: (id: string, isEnable = true) =>
        FZFR.Get(`system/project/${isEnable ? 'enable' : 'disable'}/${id}`),
    // 删除项目
    deleteProject: (id: string) => FZFR.Get(`system/project/remove/${id}`),
    // 给组织或项目添加成员
    addUserToOrgOrProject: (params: IAddUserToOrgOrProjectParams) =>
        FZFR.Post(params.projectId ? 'system/project/add-member' : '', params),
    getAdminByOrganizationOrProject: (keyword: string) => FZFR.Get<IUserOption>("system/project/user-list", {params: {keyword}})
}