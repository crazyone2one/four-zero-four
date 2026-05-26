import FZFR from "/@/api";
import type {
    ISaveGlobalUSettingData,
    ISystemUserGroupParams,
    IUserGroupAuthSetting,
    IUserGroupItem
} from "/@/typings/user-group.ts";

export const userGroupApi = {
    getUserGroupList: () => FZFR.Get<Array<IUserGroupItem>>('/user/role/global/list'),
    // 系统-删除用户组
    deleteUserGroup: (id: string) => FZFR.Get(`/user/role/global/remove/${id}`),
    // 系统-获取用户组对应的权限配置
    getGlobalUSetting: (id: string) =>
        FZFR.Get<Array<IUserGroupAuthSetting>>(`/user/role/global/permission/setting/${id}`),
    // 系统-编辑用户组对应的权限配置
    saveGlobalUSetting: (params: ISaveGlobalUSettingData) =>
        FZFR.Post('/user/role/global/permission/update', params),
    updateOrAddUserGroup: (params: ISystemUserGroupParams) =>
        FZFR.Post<IUserGroupItem>(params.id ? '/user/role/global/update' : '/user/role/global/save', params),
}