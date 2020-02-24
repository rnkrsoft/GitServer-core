package com.rnkrsoft.gitserver.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by woate on 2020/02/24.
 */
public class DateUtils {
    /**
     * 将java日期对象根据指定的格式字符串，格式化为字符串
     *
     * @param date   日期对象
     * @param format 日期样式
     * @return 日期字符串
     */
    public static String formatJavaDate2String(java.util.Date date, String format) {
        if(date == null){
            return "";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String sysdate(){
        return formatJavaDate2String(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
    }
}
