import type {FormItemType, IFieldIconAndNameModal} from "/@/typings/custom-field.ts";
import {useI18n} from "/@/composables/useI18n.ts";

const {t} = useI18n();
export const fieldIconAndName: IFieldIconAndNameModal[] = [
    {value: 'INPUT', label: t('system.orgTemplate.input'), iconName: 'i-mage:dashboard-circle-chart'},
    {key: 'TEXTAREA', iconName: 'i-mage:dashboard-circle-chart', label: t('system.orgTemplate.textarea'),},
    {value: 'RADIO', label: t('system.orgTemplate.radio'), iconName: 'i-mage:double-circle-fill'},
    {value: 'DATE', iconName: 'i-mage:calendar-2', label: t('system.orgTemplate.date'),},
    {value: 'DATETIME', iconName: 'i-mage:calendar-2', label: t('system.orgTemplate.dateTime'),},
    {value: 'NUMBER', label: t('system.orgTemplate.number'), iconName: 'i-mage:broadcast',},
    {value: 'INT', label: t('system.orgTemplate.number'), iconName: 'i-mage:broadcast',},
    {value: 'FLOAT', label: t('system.orgTemplate.number'), iconName: 'i-mage:broadcast',},
    {value: 'SELECT', label: t('system.orgTemplate.select'),},
    {value: 'MULTIPLE_SELECT', label: t('system.orgTemplate.multipleSelect'),},
    {value: 'CHECKBOX', label: t('system.orgTemplate.checkbox'),},
    {value: 'MEMBER', label: t('system.orgTemplate.member'),},
]
export const numberTypeOptions: { label: string; value: FormItemType }[] = [
    {label: '整数', value: 'INT',},
    {label: '保留小数', value: 'FLOAT',},
];
export const dateOptions: { label: string; value: FormItemType; }[] = [
    {label: 'YYYY-MM-DD', value: 'DATE',},
    {label: 'YYYY-MM-DD HH:mm:ss', value: 'DATETIME',},
];
export const getIconType = (iconType: FormItemType) => {
    return fieldIconAndName.find((item) => item.value === iconType);
};