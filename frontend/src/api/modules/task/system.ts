import FZFR from "/@/api";
import type {IPageResponse, ITableQueryParams} from "/@/typings/common";
import type {ITaskCenterSystemTaskItem} from "/@/typings/task.ts";

export const systemTaskApi = {
    // 系统任务-系统后台任务列表
    getTaskPage: (params: ITableQueryParams) =>
        FZFR.Post<IPageResponse<ITaskCenterSystemTaskItem>>('/system/task-center/schedule/page', params)
}