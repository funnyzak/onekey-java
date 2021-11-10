package com.github.funnyzak.onekey.common.utils;

import org.nutz.lang.Times;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author kerbores
 */
public class DateUtils extends Times {
    private static final String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    /**
     * 获取此为一年的第几周
     *
     * @param date
     * @param startFromSunday
     * @return
     */
    public static Integer weekOfYear(Date date, boolean startFromSunday) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(startFromSunday ? Calendar.SUNDAY : Calendar.MONDAY);
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 获取星期名称
     *
     * @param date
     * @return
     */
    public static String weekName(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return weekDays[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    /**
     * 日期加减
     *
     * @param base 开始日期
     * @param days 增加天数 正数为之后日期负数为之前日期
     * @return 目标日期
     */
    public static Date addDays(Date base, int days) {
        return D(base.getTime() + days * 24 * 60 * 60 * 1000l);
    }

    /**
     * 从今日开始的日期加减
     *
     * @param days 增加天数 正数为之后日期负数为之前日期
     * @return 目标日期
     */
    public static Date addDays(int days) {
        return addDays(now(), days);
    }

    /**
     * 两个日期之间的天数差
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 天数
     */
    public static double daysBetween(Date start, Date end) {
        double i = (end.getTime() - start.getTime()) / 1000 / 24 / 60 / 60d;
        return new BigDecimal(i).setScale(0, BigDecimal.ROUND_CEILING).doubleValue();
    }

    /**
     * 获取当日凌晨
     *
     * @param d
     * @return
     */
    public static Date getDayStart(Date d) {
        return D(format("yyyy-MM-dd 00:00:00", d));
    }

    public static Date getDayEnd(Date d) {
        return D(format("yyyy-MM-dd 23:59:59", d));
    }

    /**
     * 获取今日凌晨
     *
     * @return 今日凌晨0:0:0的时间实例
     */
    public static Date getDayStart() {
        return D(format("yyyy-MM-dd 00:00:00", now()));
    }

    public static Date getDayEnd() {
        return D(format("yyyy-MM-dd 23:59:59", now()));
    }

    public static Date getHourStart(Date d) {
        return D(format("yyyy-MM-dd HH:00:00", d));
    }

    public static Date getHourEnd(Date d) {
        return D(format("yyyy-MM-dd HH:59:59", d));
    }

    public static Date getHourStart() {
        return D(format("yyyy-MM-dd HH:00:00", now()));
    }

    public static Date getHourEnd() {
        return D(format("yyyy-MM-dd HH:59:59", now()));
    }

    public static Date getMinuteStart(Date d) {
        return D(format("yyyy-MM-dd HH:mm:00", d));
    }

    public static Date getMinuteEnd(Date d) {
        return D(format("yyyy-MM-dd HH:mm:59", d));
    }

    public static Date getMinuteStart() {
        return D(format("yyyy-MM-dd HH:mm:00", now()));
    }

    public static Date getMinuteEnd() {
        return D(format("yyyy-MM-dd HH:mm:59", now()));
    }


    /**
     * 获取当前日期的周结束<周日或者周六>
     *
     * @param startFromSunday 从周日开始计算周 标识
     * @return startFromSunday 为true 则返回下一个周六 为false则返回下一个周日
     */
    public static Date getWeekEnd(boolean startFromSunday) {
        Date date = addDays(getWeekStart(startFromSunday), 6);
        return getDayEnd(date);
    }

    public static Date getWeekEnd(boolean startFromSunday, Date date) {
        Date newDate = addDays(getWeekStart(startFromSunday, date), 6);
        return getDayEnd(newDate);
    }

    /**
     * 获取当前日期的周开始<周一或者周日>
     *
     * @param startFromSunday 从周日开始计算周 标识
     * @return startFromSunday 为true 则返回上一个周日 为false则返回上一个周一
     */
    public static Date getWeekStart(boolean startFromSunday) {
        return addDays(getDayStart(), -1 * (Times.C(now()).get(Calendar.DAY_OF_WEEK) - 1 - (startFromSunday ? 0 : 1)));
    }

    public static Date getWeekStart(boolean startFromSunday, Date date) {
        return addDays(getDayStart(date), -1 * (Times.C(date).get(Calendar.DAY_OF_WEEK) - 1 - (startFromSunday ? 0 : 1)));
    }

    /**
     * 增加秒
     *
     * @param base    基础时间
     * @param seconds 秒数
     * @return
     */
    public static Date addSeconds(Date base, long seconds) {
        return D(base.getTime() + seconds * 1000);
    }

    /**
     * 增加秒
     *
     * @param seconds 秒数
     * @return
     */
    public static Date addSeconds(long seconds) {
        return addSeconds(now(), seconds);
    }

    /**
     * 获取元旦日期
     *
     * @param d
     * @return
     */
    public static Date getYearStart(Date d) {
        String year = format("yyyy", d);
        year += "-01-01";
        return D(year);
    }

    /**
     * 获取元旦日期
     *
     * @return
     */
    public static Date getYearStart() {
        return getYearStart(now());
    }

    /**
     * 获取年末日期
     *
     * @param d
     * @return
     */
    public static Date getYearEnd(Date d) {
        String year = format("yyyy", d);
        year += "-12-31";
        return getDayEnd(D(year));
    }

    /**
     * 获取年末日期
     *
     * @return
     */
    public static Date getYearEnd() {
        return getYearEnd(now());
    }

    /**
     * 获取月初日期
     *
     * @param d
     * @return
     */
    public static Date getMonthStart(Date d) {
        String month = format("yyyy-MM", d) + "-01";
        return D(month);
    }

    /**
     * 获取月初日期
     *
     * @return
     */
    public static Date getMonthStart() {
        return getMonthStart(now());
    }

    /**
     * 获取月末日期
     *
     * @param d
     * @return
     */
    public static Date getMonthEnd(Date d) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(d);
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        return getDayEnd(ca.getTime());
    }

    public static void main(String[] args) {
        System.err.println(getWeekEnd(false));

    }

    /**
     * 获取月末日期
     *
     * @return
     */
    public static Date getMonthEnd() {
        return getMonthEnd(now());
    }

    // 获取时间相关序列
    public static String getSequence() {
        String sequence = format("yyyyMMDD", now());
        int _x = ms();
        sequence += _x % 10000;
        return sequence;
    }

    public static String getYearStartMonth() {
        return format("yyyy-MM", getYearStart());
    }

    public static String getYearStartMonth(Date date) {
        return format("yyyy-MM", getYearStart(date));
    }

    public static String getYearEndMonth() {
        return format("yyyy-MM", getYearEnd());
    }

    public static String getYearEndMonth(Date date) {
        return format("yyyy-MM", getYearEnd(date));
    }

    public static String getMonthStartDate() {
        return format("yyyy-MM-dd", getMonthStart());
    }

    public static String getMonthStartDate(Date date) {
        return format("yyyy-MM-dd", getMonthStart(date));
    }

    public static String getMonthEndDate() {
        return format("yyyy-MM-dd", getMonthEnd());
    }

    public static String getMonthEndDate(Date date) {
        return format("yyyy-MM-dd", getMonthEnd(date));
    }

    public static int getDayOfMonth() {
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        int day = aCalendar.getActualMaximum(Calendar.DATE);
        return day;
    }

    public static Long parseDate2Ts(String dateString) {
        if (StringUtils.isNullOrEmpty(dateString)) {
            return null;
        }

        try {
            if (dateString.length() == 4) {
                Integer year = TypeParse.parseInt(dateString);
                if (year != null && year > 1910) {
                    return DateUtils.parse("yyyy-MM-dd", String.format("%s-01-01", year)).getTime() / 1000;
                }
            } else if (dateString.length() >= 6 && dateString.length() <= 7) {
                return DateUtils.parse(dateString.indexOf("-") > 0 ? "yyyy-MM-dd" : "yyyy/mm/dd", String.format("%s%s01", dateString, dateString.indexOf("-") > 0 ? "-" : "/")).getTime() / 1000;
            } else {
                return DateUtils.parse(dateString.indexOf("-") > 0 ? "yyyy-MM-dd" : "yyyy/mm/dd", dateString).getTime() / 1000;
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

}
