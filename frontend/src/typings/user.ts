export type RoleType = '' | '*' | 'admin' | 'user';
export type SystemScopeType = 'PROJECT' | 'ORGANIZATION' | 'SYSTEM';

export interface IUserState {
    avatar?: string
    id?: string;
    name?: string;
    email?: string;
    phone?: string;
    lastOrganizationId?: string;
    lastProjectId?: string;
    userRolePermissions?: IUserRolePermissions[];
    userRoles?: IUserRole[];
    userRoleRelations?: IUserRoleRelation[];
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