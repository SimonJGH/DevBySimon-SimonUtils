package com.simon.utils.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.R.attr.format;
/**
 * @author Simon
 * @Description DateTimeUtils的辅助类
 * @date createTime: 2016-12-10
 */
public class DateTimeUtils {

    /**
     * 日期统一格式
     */
    private final static SimpleDateFormat sdf = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
//    private final static SimpleDateFormat sdf = new SimpleDateFormat(
//            "yyyy年MM月dd日 HH:mm:ss");

    /**
     * 获取系统当前时间{2016-05-11 13:10:08}
     *
     * @return string
     */
    public static String getCurrentTime() {

        long currentTimeMillis = System.currentTimeMillis();
        String string = sdf.format(currentTimeMillis);
        // Date date = new Date();
        // String str = format.format(date);
        return string;
    }

	
    /**
     * 将时间戳转换成日期
     * @param currentTimeMillis
     * @return
     */
    public static String convertMsecToDate(long currentTimeMillis) {
        String string = sdf.format(currentTimeMillis);
        return string;
    }

    /**
     * 将日期转换成时间戳
     *
     * @param dateTime
     * @return
     */
    public static Long convertDateToMsec(String dateTime) {
        Date date = new Date();
        try {
            date = sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * 获取下一秒的时间
     *
     * @param currentDate
     * @return
     */
    public static String getNextSecond(String currentDate) {
        String nextSecondDate = "";
        if (currentDate != null && !currentDate.equals("")) {

            try {
                Date date = sdf.parse(currentDate); // 将当前时间格式化
                // 显示输入的日期
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.SECOND, 1); // 当前时间加1秒
                date = cal.getTime();
                nextSecondDate = sdf.format(date); // 加一秒后的时间
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return nextSecondDate;
    }

    /**
     * 获取剩余时间 几天几时几分几秒
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static String getRemainTime(String startTime, String endTime) {
        String remainTime = "0"; // 剩余时间
        long dayMsec = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long hourMsec = 1000 * 60 * 60;// 一小时的毫秒数
        long minuteMsec = 1000 * 60;// 一分钟的毫秒数
        long secondMsec = 1000;// 一秒钟的毫秒数
        long diffMsec; // 毫秒差

        if (startTime != null && !startTime.equals("") && endTime != null
                && !endTime.equals("")) {
            try {
                // 获得两个时间的毫秒时间差异
                diffMsec = sdf.parse(endTime).getTime()
                        - sdf.parse(startTime).getTime();
                if (diffMsec > 0) {
                    /* 判断结束时间是否大于开始时间 */
                    long diffDay = diffMsec / dayMsec;// 计算差多少天
                    long diffHour = diffMsec % dayMsec / hourMsec;// 计算差多少小时
                    long diffMin = diffMsec % dayMsec % hourMsec / minuteMsec;// 计算差多少分钟
                    long diffSec = diffMsec % dayMsec % dayMsec % minuteMsec
                            / secondMsec;// 计算差多少秒//输出结果
                    remainTime = diffDay + "天" + diffHour + "时" + diffMin + "分"
                            + diffSec + "秒";
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return remainTime;
    }

}
