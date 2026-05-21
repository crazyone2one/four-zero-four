import type {IUserState} from "/@/typings/user.ts";

export interface ITableQueryParams {
    // 当前页
    page?: number;
    // 每页条数
    pageSize?: number;
    // 排序仅针对单个字段
    sort?: object;
    // 排序仅针对单个字段
    sortString?: string;
    // 表头筛选
    filter?: object;
    // 查询条件
    keyword?: string;

    [key: string]: string | number | object | undefined;
}

export interface IPageResponse<T> {
    [x: string]: number | T[];

    pageSize: number;
    totalPage: number;
    pageNumber: number;
    totalRow: number;
    records: T[];
}
export interface ILogin {
    username: string
    password: string
}
export interface IAuthenticationResponse {
    accessToken: string;
    refreshToken: string;
    user?: IUserState;
}
export interface IBatchApiParams {
    selectIds: string[]; // 已选 ID 集合，当 selectAll 为 false 时接口会使用该字段
    excludeIds?: string[]; // 需要忽略的用户 id 集合，当selectAll为 true 时接口会使用该字段
    selectAll: boolean; // 是否跨页全选，即选择当前筛选条件下的全部表格数据
    condition: Record<string, any>; // 当前表格查询的筛选条件
    currentSelectCount?: number; // 当前已选择的数量
    projectId?: string; // 项目 ID
    moduleIds?: (string | number)[]; // 模块 ID 集合
    versionId?: string; // 版本 ID
    refId?: string; // 版本来源
    protocols?: string[]; // 协议集合
}