import type {FormItemRule, SelectGroupOption, SelectOption} from "naive-ui";

export type FormItemType = 'input' | 'select' | 'inputNumber' | 'tagInput' | 'multiple' | 'switch' | 'textarea';
export type FormMode = 'create' | 'edit';

export interface CustomValidator {
    notRepeat?: boolean;
}

export interface IFormItemModel {
    field: string;
    type: FormItemType;
    label?: string;
    placeholder?: string;
    defaultValue?: string | string[] | number | number[] | boolean; // 默认值
    children?: IFormItemModel[];
    rules?: (FormItemRule & CustomValidator)[];
    options?: Array<SelectOption | SelectGroupOption>
}