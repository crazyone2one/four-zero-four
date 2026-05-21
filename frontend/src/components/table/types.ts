import type {DropdownOption} from "naive-ui";
import type {ITableQueryParams} from "/@/typings/common";

export interface IBatchActionConfig {
    baseAction: Array<DropdownOption>;
    moreAction?: Array<DropdownOption>;
}

export interface IBatchActionQueryParams {
    excludeIds?: string[]; // 排除的id
    selectedIds?: string[];
    selectAll: boolean; // 是否跨页全选
    params?: ITableQueryParams; // 查询参数
    currentSelectCount?: number; // 当前选中的数量
    condition?: any; // 查询条件
    [key: string]: any;
}