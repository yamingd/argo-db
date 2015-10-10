package com.argo.db.exception;

/**
 * Created by yamingd on 9/15/15.
 */
public class EntityNotFoundException extends Exception {

    private int errorCode = 60404;

    public EntityNotFoundException(String table, Object pkValue) {
        super("Can't find Record. table=" + table + ", pk=" + pkValue);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
