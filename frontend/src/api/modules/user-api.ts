import FZFR from "/@/api";
import type {
    ICreateUserParams,
    IDeleteUserParams,
    ISystemRole,
    IUpdateUserStatusParams,
    IUserState
} from "/@/typings/user.ts";
import type {IPageResponse, ITableQueryParams} from "/@/typings/common";

export const userApi = {
    updateLanguage: (data: { language: string }) => FZFR.Post('/personal/update-locale', data),
    batchCreateUser: (data: ICreateUserParams) => FZFR.Post<{
        errorEmails: Record<string, string>
    }>('/system/user/save', data),
    getSystemRoles: () => FZFR.Get<ISystemRole[]>('system/user/get/global/system/role'),
    getUserPage: (data: ITableQueryParams) => FZFR.Post<IPageResponse<IUserState>>('system/user/page', data),
    // 更新用户启用/禁用状态
    toggleUserStatus: (data: IUpdateUserStatusParams) => FZFR.Post('/system/user/update/enable', data),
    // 删除用户
    deleteUserInfo: (data: IDeleteUserParams) => FZFR.Post('/system/user/remove', data)
}