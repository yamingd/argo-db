package com.argo.db.exception;

/**
 * Created by yamingd on 9/15/15.
 */
public class EntityNotFoundException extends Exception {

    public EntityNotFoundException(String table, Object pkValue) {
        super("Can't find Record. table=" + table + ", pk=" + pkValue);
    }

}
