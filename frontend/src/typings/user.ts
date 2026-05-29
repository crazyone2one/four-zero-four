import type {IBatchApiParams} from "/@/typings/common";
import type {SelectOption} from "naive-ui";

export type RoleType = '' | '*' | 'admin' | 'user';
export type SystemScopeType = 'PROJECT' | 'ORGANIZATION' | 'SYSTEM';

export interface IUserState {
    avatar?: string
    id?: string;
    name: string;
    email?: string;
    phone?: string;
    enable?: boolean;
    lastOrganizationId?: string;
    lastProjectId?: string;
    userRolePermissions?: IUserRolePermissions[];
    userRoles?: IUserRole[];
    userRoleList?: IUserRole[];
    userRoleRelations?: IUserRoleRelation[];
    selectUserGroupVisible?: boolean;
}

export interface IUserRolePermissions {
    userRole: IUserRole;
    userRolePermissions: IPermissionsItem[];
}

export interface IPermissionsItem {
    id: string;
    permissionId: string;
    roleId: string;
}

export interface IUserRole {
    createTime: number;
    updateTime: number;
    createUser: string;
    description?: string;
    id: string;
    name: string;
    scopeId: string; // 项目/组织/系统 id
    type: SystemScopeType;
}

export interface IUserRoleRelation {
    id: string;
    userId: string;
    roleId: string;
    sourceId: string;
    organizationId: string;
    createTime: number;
    createUser: string;
    userRolePermissions: IPermissionsItem[];
    userRole: IUserRole;
}

export interface ISimpleUserInfo {
    id?: string;
    name: string;
    email: string;
    phone?: string;
}

export type IUserOption = ISimpleUserInfo & SelectOption

export interface ICreateUserParams {
    userInfoList: ISimpleUserInfo[];
    userRoleIdList: string[];
}

export interface ISystemRole extends SelectOption {
    id: string;
    name: string;
    selected: boolean; // 是否可选
    closeable: boolean; // 是否可取消
}

export interface IUpdateUserStatusParams extends IBatchApiParams {
    enable: boolean;
}

export type IDeleteUserParams = IBatchApiParams;