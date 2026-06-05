export interface ITaskDetailInfo extends IUpdateTaskInfo {
    organizationName?: string; // 所属组织名称
    projectName?: string; // 所属项目名称
    organizationId?: string; // 组织ID
    reportId?: string;
    resourceId: string; // 资源ID
    num: number;
    resourceType: string; // 资源类型
    resourceNum: number; // 资源num
    nextTime: string;
    createUser: string;
    createUserName?: string;
    createTime: number;
    runRuleLoading?: boolean

    [key: string]: any;
}

export interface IUpdateTaskInfo {
    id?: string;
    name: string;
    value: string;
    executorHandler?: string;
    projectId?: string;
    job?: string;
    enable: boolean;
}