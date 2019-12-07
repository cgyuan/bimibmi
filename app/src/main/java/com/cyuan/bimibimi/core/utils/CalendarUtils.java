package com.cyuan.bimibimi.core.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarUtils {

    public static Calendar getCalendar(String dateStr) {
        Calendar calendar = Calendar.getInstance();
        Date date;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        if (dateStr != null) {
            try {
                //解析日期字符串，生成Date对象
                date = format.parse(dateStr);
                // 使用Date对象设置此Calendar对象的时间
                calendar.setTime(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return calendar;
    }

    /**
     * 计算两个日期之间相差的天数(如果两个日期是一天则返回0)
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getDaysBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return -1;
        } else if (startDate.equals(endDate)) {
            return 0;
        }
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        start.setTime(startDate);
        end.setTime(endDate);

        //设置时间为0时
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
//        end.set(Calendar.HOUR_OF_DAY, 0);
//        end.set(Calendar.MINUTE, 0);
//        end.set(Calendar.SECOND, 0);
        //得到两个日期相差的天数

        int days = ((int) (end.getTime().getTime() / 1000) - (int) (start.getTime().getTime() / 1000)) / 3600 / 24;
        return days;
    }

    /**
     * 获取简单时间格式(年-月-日)
     *
     * @return
     */
    private static DateFormat getSimpleFormat() {
        return getFormat(TimeFormat.yyyy_MM_dd.getTimeFormat());
    }

    private static DateFormat getFormat(String format) {
        return new SimpleDateFormat(format);
    }

    public static String[] getDateRange(int days) {
//        Date endDate = new Date();
//        long time = endDate.getTime();
//        long startTimeMils = time / 1000 - days * 24 * 3600L;
//        Date startDate = new Date(startTimeMils * 1000);
//        return new String[] { dateToStr(startDate, TimeFormat.yyyy_MM_dd), dateToStr(endDate, TimeFormat.yyyy_MM_dd)};

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        end.set(Calendar.HOUR_OF_DAY, 0);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);

        start.add(Calendar.DAY_OF_YEAR, -days);
        return new String[] { dateToStr(start.getTime(), TimeFormat.yyyy_MM_dd), dateToStr(end.getTime(), TimeFormat.yyyy_MM_dd)};
    }

    /**
     * 根据给定格式,把日期转换成字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateToStr(Date date, TimeFormat format) {
        return getFormat(format.toString()).format(date);
    }

    /**
     * 根据给定格式,把字符串转换为日期
     *
     * @param dateStr
     * @param format
     * @return
     */
    public static Date strToDate(String dateStr, TimeFormat format) {
        Date d = null;
        try {
            d = getFormat(format.toString()).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    public enum TimeFormat {
        HH_mm_ss("HH:mm:ss"),
        HH_mm("HH:mm"),
        mm_ss("mm:ss"),
        HHmmss("HHmmss"),
        HHmm("HHmm"),
        HH("HH"),
        mmss("mmss"),
        mm("mm"),
        ss("ss"),
        yyyy_MM_dd__HH_mm_ss("yyyy-MM-dd HH:mm:ss"),
        yyyy_MM_dd__HH_mm("yyyy-MM-dd HH:mm"),
        yyyy_MM_dd__HH("yyyy-MM-dd HH"),
        yyyyMMddHHmmss("yyyyMMddHHmmss"),
        yyyyMMddHHmm("yyyyMMddHHmm"),
        yyyyMMddHH("yyyyMMddHH"),
        yyyyMMdd("yyyyMMdd"),
        yyyy_MM_dd("yyyy-MM-dd"),
        yyyy_MM("yyyy-MM"),
        yyyy_M_d("yyyy-M-d"),
        yyyyMM("yyyyMM"),
        yyyy_slash_MM("yyyy/MM"),
        yy_slash_MM_slash_dd("yy/MM/dd"),
        yyyy("yyyy"),
        MM("MM"),
        dd("dd");

        private String format;

        TimeFormat(String format) {
            this.format = format;
        }

        public static TimeFormat forFormat(String format) {
            for (TimeFormat c : values()) {
                if (c.toString().equals(format)) {
                    return c;
                }
            }
            return null;
        }

        public String toString() {
            return this.format;
        }

        public String getTimeFormat() {
            return this.format;
        }
    }
}
