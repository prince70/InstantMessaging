package com.niwj.control;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

/**
 * TimeUtils
 * Created by Yancy on 2015/12/2.
 */
public class TimeUtils {

    private final static String TAG = "TimeUtils";

    private final static String PATTERN = "yyyy-MM-dd";

    public static String timeFormat(long timeMillis, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
        return simpleDateFormat.format(new Date(timeMillis));
    }


    public static String formatPhotoDate(long time) {
        return timeFormat(time, PATTERN);
    }

    public static String formatPhotoDate(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            long time = file.lastModified();
            return formatPhotoDate(time);
        }
        return "1970-01-01";
    }

    private static DateTime now = DateTime.now(TimeZone.getDefault());

    public static int getNowYear() {
        return now.getYear();
    }

    public static int getNowMonth() {
        return now.getMonth();
    }

    public static int getNowDay() {
        return now.getDay();
    }

    public static int getNowHour() {
        return now.getHour();
    }

    public static int getNowMinute() {
        return now.getMinute();
    }

    /**
     * YYYY-MM-DD
     *
     * @return
     */
    public static String getNowDate() {
        return now.format("YYYY-MM-DD");
    }

    /**
     * YYYY年MM月DD日
     *
     * @return
     */
    public static String getNowDateCN() {
        return now.format("YYYY年MM月DD日");
    }

    /**
     * MM月DD日
     *
     * @return
     */
    public static String getNowMMDD() {
        return now.format("MM月DD日");
    }

    /**
     * HH:MM
     *
     * @return
     */
    public static String getNowTime() {
        return now.format("HH:MM");
    }

    public static Date getHHDDTime(String timeString) {
        Date startTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:MM");
        try {

            startTime = sdf.parse(timeString);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startTime;
    }

    public static long getMilliseconds() {
        return now.getMilliseconds(TimeZone.getDefault());
    }

    public static String getMMDDByMill(long milliseconds) {
        return DateTime.forInstant(milliseconds, TimeZone.getDefault()).format("MM月DD日");
    }

    public static String getYYYYMMDDByMill(long milliseconds) {
        return DateTime.forInstant(milliseconds, TimeZone.getDefault()).format("YYYY年MM月DD日");
    }

    /**
     * 格式HH:mm
     *
     * @param time
     */
    public static int[] getHHmmTime(String time) {
        int[] hhmm = new int[2];
        Integer hourInt = Integer.valueOf(time.substring(0, 2));
        hhmm[0] = hourInt;
        Integer minInt = Integer.valueOf(time.substring(3, 5));
        hhmm[1] = minInt;
        return hhmm;
    }

}