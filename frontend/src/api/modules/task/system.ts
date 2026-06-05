import FZFR from "/@/api";
import type {IPageResponse, ITableQueryParams} from "/@/typings/common";
import type {ITaskDetailInfo} from "/@/typings/task.ts";

export const systemTaskApi = {
    // 系统任务-系统后台任务列表
    getTaskPage: (params: ITableQueryParams) =>
        FZFR.Post<IPageResponse<ITaskDetailInfo>>('/system/task-center/schedule/page', params, {
            transform(rowData, _) {
                const data = rowData as IPageResponse<ITaskDetailInfo>
                data.records.forEach(item => {
                    item.runRuleLoading = false
                })
                return data
            }
        }),
    editCron: (data: { id: string, cron: string }) => FZFR.Post('/schedule/update-cron', data),
    scheduleSwitch: (id: string) => FZFR.Get(`/schedule/switch/${id}`)
}