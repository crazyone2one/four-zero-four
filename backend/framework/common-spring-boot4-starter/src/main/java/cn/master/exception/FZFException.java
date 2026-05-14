package cn.master.exception;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : 11's papa
 * @since : 2026/5/13, 星期三
 **/
@Getter
public class FZFException extends RuntimeException {
    protected IResultCode errorCode;

    public FZFException(String message) {
        super(message);
    }

    public FZFException(Throwable t) {
        super(t);
    }

    public FZFException(IResultCode errorCode) {
        super(StringUtils.EMPTY);
        this.errorCode = errorCode;
    }

    public FZFException(IResultCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public FZFException(IResultCode errorCode, Throwable t) {
        super(t);
        this.errorCode = errorCode;
    }

    public FZFException(String message, Throwable t) {
        super(message, t);
    }

}
