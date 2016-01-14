package com.argo.db;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by yamingd on 9/27/15.
 */
public class Values {

    /**
     * 获取 ResultSet列的值
     * @param rs ResultSet
     * @param index index
     * @param requiredType requiredType
     * @return T item
     * @throws SQLException SQLException
     */
    public static <T> T getResultSetValue(ResultSet rs, int index, Class requiredType) throws SQLException {
        Preconditions.checkNotNull(requiredType);

        Object value = null;
        boolean wasNullCheck = false;

        // Explicitly extract typed value, as far as possible.
        if (String.class.equals(requiredType)) {
            value = rs.getString(index);
        }
        else if (boolean.class.equals(requiredType) || Boolean.class.equals(requiredType)) {
            value = rs.getBoolean(index);
            wasNullCheck = true;
        }
        else if (byte.class.equals(requiredType) || Byte.class.equals(requiredType)) {
            value = rs.getByte(index);
            wasNullCheck = true;
        }
        else if (short.class.equals(requiredType) || Short.class.equals(requiredType)) {
            value = rs.getShort(index);
            wasNullCheck = true;
        }
        else if (int.class.equals(requiredType) || Integer.class.equals(requiredType)) {
            value = rs.getInt(index);
            wasNullCheck = true;
        }
        else if (long.class.equals(requiredType) || Long.class.equals(requiredType)) {
            value = rs.getLong(index);
            wasNullCheck = true;
        }
        else if (float.class.equals(requiredType) || Float.class.equals(requiredType)) {
            value = rs.getFloat(index);
            wasNullCheck = true;
        }
        else if (double.class.equals(requiredType) || Double.class.equals(requiredType) ||
                Number.class.equals(requiredType)) {
            value = rs.getDouble(index);
            wasNullCheck = true;
        }
        else if (byte[].class.equals(requiredType)) {
            value = rs.getBytes(index);
        }
        else if (java.sql.Date.class.equals(requiredType)) {
            value = rs.getDate(index);
        }
        else if (java.sql.Time.class.equals(requiredType)) {
            value = rs.getTime(index);
        }
        else if (java.sql.Timestamp.class.equals(requiredType) || java.util.Date.class.equals(requiredType)) {
            value = rs.getTimestamp(index);
        }
        else if (BigDecimal.class.equals(requiredType)) {
            value = rs.getBigDecimal(index);
        }
        else if (Blob.class.equals(requiredType)) {
            value = rs.getBlob(index);
        }
        else if (Clob.class.equals(requiredType)) {
            value = rs.getClob(index);
        }

        // Perform was-null check if demanded (for results that the
        // JDBC driver returns as primitives).
        if (wasNullCheck && value != null && rs.wasNull()) {
            value = null;
        }

        if (value == null){
            return null;
        }

        return (T) value;
    }

    /**
     * 转换为Java类型值
     * @param value value
     * @param requiredType requiredType
     * @param <T> T
     * @return T
     */
    public static <T> T get(Object value, Class requiredType){
        if (null == value){
            return null;
        }
        if (value.getClass().equals(requiredType)){
            return (T) value;
        }
        if (java.util.Date.class.equals(requiredType)){
            if (value.getClass().equals(Integer.class)){
                Integer ts = (Integer)value;
                Date ret = new Date(ts.longValue() * 1000);
                return (T)ret;
            }else if (value.getClass().equals(Long.class)){
                Long ts = (Long)value;
                Date ret = new Date(ts.longValue() * 1000);
                return (T)ret;
            }
        }else if (Integer.class.equals(requiredType)){
            if (java.util.Date.class.equals(value.getClass())){
                Date d = (Date)value;
                Long lvalue = d.getTime() / 1000;
                Integer ret = lvalue.intValue();
                return (T)(ret);
            }else if (Long.class.equals(value.getClass())){
                Integer ret = ((Long)value).intValue();
                return (T)(ret);
            }
        }else if (Long.class.equals(requiredType)){
            if (java.util.Date.class.equals(value.getClass())){
                Date d = (Date)value;
                Long lvalue = d.getTime();
                return (T)(lvalue);
            }else if (Integer.class.equals(value.getClass())){
                Long ret = ((Integer)value).longValue();
                return (T)(ret);
            }
        }
        return null;
    }

    /**
     * 统一时间戳
     * @param date date
     * @return Integer
     */
    public static Integer getEpoch(Date date){
        if (null == date){
            return null;
        }
        return (int) (date.getTime() / 1000);
    }

    /**
     * 统一时间戳
     * @return int
     */
    public static int getNow(){
        return (int) (System.currentTimeMillis() / 1000);
    }

    /**
     * 统一时间戳
     * @return Long
     */
    public static Long getNowMs(){
        return System.currentTimeMillis();
    }

    public static void main(String[] args){
        Date date = get(1448632229, Date.class);
        System.out.println(date);
        Integer intValue = get(date, Integer.class);
        System.out.println(intValue);
    }
}
