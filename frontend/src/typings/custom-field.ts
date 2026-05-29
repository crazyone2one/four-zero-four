import type {LocationQueryValue} from "vue-router";
import type {SelectOption} from "naive-ui";

export type FormItemType =
    | 'INPUT'
    | 'TEXTAREA'
    | 'SELECT'
    | 'MULTIPLE_SELECT'
    | 'RADIO'
    | 'CHECKBOX'
    | 'MEMBER'
    | 'MULTIPLE_MEMBER'
    | 'DATE'
    | 'DATETIME'
    | 'INT'
    | 'FLOAT'
    | 'MULTIPLE_INPUT'
    | 'INT'
    | 'FLOAT'
    | 'NUMBER'
    | 'PassWord'
    | 'CASCADER'
    | undefined;
export type SceneType = 'FUNCTIONAL' | 'BUG' | 'API' | 'UI' | 'TEST_PLAN' | LocationQueryValue[] | LocationQueryValue;

export interface IFieldOptions {
    fieldId?: string;
    value: any;
    text: string;
    internal?: boolean;
    pos?: number;
    id?: string;
}

export interface ICustomField {
    id: string;
    name: string;
    scene: SceneType; // 使用场景
    type: FormItemType; // 表单类型
    remark: string;
    internal: boolean; // 是否是内置字段
    scopeType: string; // 组织或项目级别字段（PROJECT, ORGANIZATION）
    createTime: number;
    updateTime: number;
    createUser: string;
    refId: string | null; // 项目字段所关联的组织字段ID
    enableOptionKey: boolean | null; // 是否需要手动输入选项key
    scopeId: string; // 组织或项目ID
    options: Array<IFieldOptions>;
}

export interface IAddOrUpdateField {
    id?: string;
    name: string;
    used: boolean;
    scene: SceneType; // 使用场景
    type: FormItemType;
    remark: string; // 备注
    scopeId: string; // 组织或项目ID
    options?: Array<IFieldOptions>;
    enableOptionKey: boolean;

    [key: string]: any;
}

export interface IFieldIconAndNameModal extends SelectOption {
    iconName?: string; // 图标名称
}