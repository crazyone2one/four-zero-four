import type {AuthScopeEnumType} from "/@/typings/common";

export type AuthScopeType =
    | 'SYSTEM'
    | 'PROJECT'
    | 'ORGANIZATION'
    | 'WORKSTATION'
    | 'TEST_PLAN'
    | 'BUG_MANAGEMENT'
    | 'CASE_MANAGEMENT'
    | 'API_TEST'
    | 'UI_TEST'
    | 'LOAD_TEST'
    | 'PERSONAL';

export interface IBaseUserGroup {
    id: string;
    name: string;
    type: AuthScopeEnumType;
    internal: boolean;
    scopeId?: string;
}

export interface ISystemUserGroupParams {
    id?: string; // 组ID
    name?: string; // 名称
    scopeId?: string; // 组织ID
    type?: string; // 组类型：SYSTEM | PROJECT | ORGANIZATION
}

export interface IUserGroupItem extends IBaseUserGroup {
    description: string;
    pos: number;
}

export interface IUserGroupPermissionItem {
    id: string;
    name: string;
    enable: boolean;
    // license: boolean;
}

export interface IUserGroupAuthSetting {
    id: AuthScopeType;
    type?: string;
    name: string;
    enable: boolean;
    permissions?: IUserGroupPermissionItem[];
    children?: IUserGroupAuthSetting[];
}

export interface ISavePermissions {
    id: string;
    enable: boolean;
}

export interface ISaveGlobalUSettingData {
    userRoleId: string;
    permissions: ISavePermissions[];
}

export interface PopVisibleItem {
    id?: string;
    visible: boolean;
    authScope: AuthScopeEnumType;
    defaultName: string;
}
export interface PopVisible {
    [key: string]: PopVisibleItem;
}
export interface IAuthTableItem {
    id: string;
    name?: string;
    enable: boolean;
    // license: boolean;
    ability?: string | undefined;
    permissions?: IUserGroupPermissionItem[];
    // 对应表格权限的复选框组的绑定值
    perChecked?: string[];
    operationObject?: string;
    rowSpan?: number;
    indeterminate?: boolean;
}