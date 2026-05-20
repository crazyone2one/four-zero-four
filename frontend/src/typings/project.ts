import type {SelectOption} from "naive-ui";

export interface IProjectBaseItem {
    id: string;
    num: number;
    organizationId: string;
    name: string;
    description: string;
}

export type IProjectOption = IProjectBaseItem & SelectOption

export interface ICreateOrUpdateSystemProjectParams {
    id?: string;
    // 项目名称
    name: string;
    num: string;
    // 项目描述
    description: string;
    // 启用或禁用
    enable: boolean;
    // 项目成员
    userIds: string[];
    // 模块配置
    moduleIds?: string[];
    // 所属组织
    organizationId?: string;
    // 资源池
    resourcePoolIds?: string[];
    // 列表里的
    allResourcePool?: boolean; // 默认全部资源池
}