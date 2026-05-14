package cn.master.constants;

/**
 * @author : 11's papa
 * @since : 2026/5/13, 星期三
 **/
public enum OperationLogType {
    ADD,
    DELETE,
    UPDATE,
    DEBUG,
    REVIEW,
    COPY,
    EXECUTE,
    SHARE,
    RESTORE,
    IMPORT,
    EXPORT,
    LOGIN,
    SELECT,
    RECOVER,
    LOGOUT,
    DISASSOCIATE,
    ASSOCIATE,
    QRCODE,
    ARCHIVED,
    STOP,
    RERUN;

    public boolean contains(OperationLogType keyword) {
        return this.name().contains(keyword.name());
    }
}
