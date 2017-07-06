package com.aiminerva.oldpeople.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class TimeUtil {

    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(new Date(time));
    }

    public static String getTimeCounter(long time) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(new Date(time));
    }

    public static String getYMDHMS(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(time));
    }


    public static String getMdhm(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        return format.format(new Date(time));
    }

    public static String getYmd(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(time));
    }

    public static String getYyr(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(new Date(time));
    }

    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    public static String getYear(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        return format.format(new Date(time));
    }

    public static String getMonth(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MM");
        return format.format(new Date(time));
    }

    public static String getDay(long time) {
        SimpleDateFormat format = new SimpleDateFormat("dd");
        return format.format(new Date(time));
    }

    public static String getHour(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH");
        return format.format(new Date(time));
    }

    public static String getMinute(long time) {
        SimpleDateFormat format = new SimpleDateFormat("mm");
        return format.format(new Date(time));
    }

    public static String getChatTime(long timesamp) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        SimpleDateFormat month_mat = new SimpleDateFormat("mm");
        SimpleDateFormat year_mat = new SimpleDateFormat("yyyy");
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(timesamp);
        int temp_year = Integer.parseInt(year_mat.format(today))
                - Integer.parseInt(year_mat.format(otherDay));

        int temp_month = Integer.parseInt(month_mat.format(today))
                - Integer.parseInt(month_mat.format(otherDay));

        int temp = Integer.parseInt(sdf.format(today))
                - Integer.parseInt(sdf.format(otherDay));
        if (temp_year == 0) {
            if (temp_month == 0) {
                switch (temp) {
                    case 0:
                        result = "今天 " + getHourAndMin(timesamp);
                        break;
                    case 1:
                        result = "昨天 " + getHourAndMin(timesamp);
                        break;
                    case 2:
                        result = "前天 " + getHourAndMin(timesamp);
                        break;

                    default:
                        // result = temp + "天前 ";

                        break;
                }
            } else {
                result = getYmd(timesamp);
            }
        } else {
            result = getYmd(timesamp);
        }

        return result;
    }

    public static String getChatTimeHasHm(long timesamp) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(timesamp);
        int temp = Integer.parseInt(sdf.format(today))
                - Integer.parseInt(sdf.format(otherDay));

        switch (temp) {
            case 0:
                result = "今天 " + getHourAndMin(timesamp);
                break;
            case 1:
                result = "昨天 " + getHourAndMin(timesamp);
                break;
            case 2:
                result = "前天 " + getHourAndMin(timesamp);
                break;

            default:
                // result = temp + "天前 ";
                result = getTime(timesamp);
                break;
        }

        return result;
    }

    public static long dateFormat(String date) {
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(date
                    .replace("-", "") + "000000"));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // System.out.println("时间转化后的毫秒数为：" + c.getTimeInMillis());
        return c.getTimeInMillis();
    }

    public static long dateFormat1(String date) {
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(date
                    .replace("-", "") + "235959"));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // System.out.println("时间转化后的毫秒数为：" + c.getTimeInMillis());
        return c.getTimeInMillis();
    }


    public static long TimeCount(String date) {
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(new SimpleDateFormat("mm\"ss\"").parse(date));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // System.out.println("时间转化后的毫秒数为：" + c.getTimeInMillis());
        return c.getTimeInMillis();
    }
}
