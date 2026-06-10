import type {SelectOption} from "naive-ui";

export interface IProjectBaseItem {
    id: string;
    num: string;
    organizationId: string;
    name: string;
    description: string;
    enable: boolean;
}

export type IProjectOption = IProjectBaseItem & SelectOption

export interface ICreateOrUpdateSystemProjectParams {
    id?: string;
    name: string;
    num: string;
    description: string;
    enable: boolean;
    userIds: string[];
    organizationId?: string;
}

export interface IProjectPageItem extends IProjectBaseItem {
    memberCount: number;
    organizationName: string;
    adminList: AdminList[];
    projectCreateUserIsAdmin: boolean;
    createUser?: string;
}

export interface AdminList {
    id: string;
    name: string;
    email: string;
    password: string;
    enable: boolean;
    createTime: string;
    updateTime: number;
    language: string;
    lastOrganizationId: string;
    phone: string;
    source: string;
    lastProjectId: string;
    createUser: string;
    updateUser: string;
    deleted: boolean; // 是否删除
    adminFlag: boolean; // 是否组织/项目管理员
    memberFlag: boolean; // 是否组织/项目成员
    checkRoleFlag: boolean; // 是否属于用户组
    sourceId: string; // 资源id
}

export interface IAddUserToOrgOrProjectParams {
    userIds?: string[];
    organizationId?: string;
    projectId?: string;
    // 等待接口改动 将要废弃，以后用userIds
    memberIds?: string[];
    userRoleIds?: string[];
}

export interface IDataSource {
    dataSource: string
    dbUrl: string
    username: string
    password: string
}

export interface IProjectParams {
    id: string;
    projectId: string;
    paramType: string;
    parameters: IDataSource;
}