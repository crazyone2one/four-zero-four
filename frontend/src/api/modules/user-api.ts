import FZFR from "/@/api";

export const userApi = {
    updateLanguage: (data: { language: string }) => FZFR.Post('/personal/update-locale', data),
}