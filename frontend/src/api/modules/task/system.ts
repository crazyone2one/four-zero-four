import FZFR from "/@/api";
import type {IPageResponse, ITableQueryParams} from "/@/typings/common";
import type {ITaskDetailInfo, IUpdateTaskInfo} from "/@/typings/task.ts";
import dayjs from "dayjs";

export const systemTaskApi = {
    // 系统任务-系统后台任务列表
    getTaskPage: (params: ITableQueryParams) =>
        FZFR.Post<IPageResponse<ITaskDetailInfo>>('/schedule/page', params, {
            transform(rowData, _) {
                const data = rowData as IPageResponse<ITaskDetailInfo>
                data.records.forEach(item => {
                    item.runRuleLoading = false;
                    item.lastTime = item.lastTime && item.lastTime !== -1 ? dayjs(item.lastTime).format('YYYY-MM-DD HH:mm:ss') : '-';
                    item.nextTime = item.nextTime ? dayjs(item.nextTime).format('YYYY-MM-DD HH:mm:ss') : '-'
                })
                return data
            }
        }),
    editCron: (data: { id: string, cron: string }) => FZFR.Post('/schedule/update-cron', data),
    scheduleSwitch: (id: string) => FZFR.Get(`/schedule/switch/${id}`),
    saveOrUpdateTask: (data: IUpdateTaskInfo) => FZFR.Post(data.id ? '/schedule/update' : '/schedule/save', data),
}