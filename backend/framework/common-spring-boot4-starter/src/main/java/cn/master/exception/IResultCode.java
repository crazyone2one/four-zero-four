package cn.master.exception;

import cn.master.util.Translator;

/**
 * @author : 11's papa
 * @since : 2026/5/12, 星期二
 **/
public interface IResultCode {
    int getCode();
    String getMessage();
    default String getTranslationMessage(String message) {
        return Translator.get(message, message);
    }
}
