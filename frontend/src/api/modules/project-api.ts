import FZFR from "/@/api";
import type {ICreateOrUpdateSystemProjectParams, IProjectOption} from "/@/typings/project.ts";

export const projectApi = {
    getProjectList: (organizationId: string) => FZFR.Get<Array<IProjectOption>>(`/project/list/options/${organizationId}`),
    createProject: (data: ICreateOrUpdateSystemProjectParams) => FZFR.Post(data.id ? '/project/update' : `/project/save`, data),
    switchProject: (data: { projectId: string, userId: string }) => FZFR.Post('/project/switch', data),
}