import FZFR from "/@/api";
import type {ICreateUserParams, ISystemRole, IUserState} from "/@/typings/user.ts";
import type {IPageResponse, ITableQueryParams} from "/@/typings/common";

export const userApi = {
    updateLanguage: (data: { language: string }) => FZFR.Post('/personal/update-locale', data),
    batchCreateUser: (data: ICreateUserParams) => FZFR.Post<{
        errorEmails: Record<string, string>
    }>('/system/user/save', data),
    getSystemRoles: () => FZFR.Get<ISystemRole[]>('system/user/get/global/system/role'),
    getUserPage: (data: ITableQueryParams) => FZFR.Post<IPageResponse<IUserState>>('system/user/page', data)
}