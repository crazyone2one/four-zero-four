import FZFR from "/@/api";
import type {IAddOrUpdateField, ICustomField} from "/@/typings/custom-field.ts";
import type {IPageResponse, ITableQueryParams} from "/@/typings/common";

export const customFieldApi = {
    addOrUpdateOrdField: (params: IAddOrUpdateField) => FZFR.Post<ICustomField>(
        params.id ? '/custom/field/update' : '/custom/field/save', params
    ),
    deleteOrdField: (id: string) => FZFR.Get(`custom/field/remove/${id}`),
    getFieldPage: (params: ITableQueryParams) => FZFR.Post<IPageResponse<ICustomField>>('custom/field/page', params),
}