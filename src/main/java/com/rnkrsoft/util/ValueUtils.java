package com.rnkrsoft.util;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by woate on 2018/5/8.
 */
public class ValueUtils {
    public static <T> T convert(Object value, Class<T> type) {
        if (type == String.class) {
            return (T) value;
        }
        if (type == Boolean.TYPE || type == Boolean.class) {
            Boolean val = (Boolean) value;
            return (T) val;
        }
        if (type == Byte.TYPE || type == Byte.class) {
            Byte val = (Byte) value;
            return (T) val;
        }
        if (type == Short.TYPE || type == Short.class) {
            Short val = (Short) value;
            return (T) val;
        }
        if (type == Integer.TYPE || type == Integer.class) {
            Integer val = (Integer) value;
            return (T) val;
        }
        if (type == Long.TYPE || type == Long.class) {
            Long val = (Long) value;
            return (T) val;
        }
        if (type == Float.TYPE || type == Float.class) {
            Float val = (Float) value;
            return (T) val;
        }
        if (type == Double.TYPE || type == Double.class) {
            Double val = (Double) value;
            return (T) val;
        }
        if (type == java.util.Date.class) {
            java.util.Date val = (Date) value;
            return (T) val;
        }
        if (type == BigDecimal.class) {
            BigDecimal val = (BigDecimal) value;
            return (T) val;
        }
        return (T) value;
    }
}