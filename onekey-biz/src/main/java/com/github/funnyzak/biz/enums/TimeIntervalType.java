package com.github.funnyzak.biz.enums;

import lombok.Data;
import lombok.Getter;
import com.github.funnyzak.common.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 时间间隔类型，主要用来获取统计图表时间线步长
 */
public enum TimeIntervalType {
    MINUTE("分钟", 60, "HH:mm"),
    HOUR("小时", 3600, "H时"),
    DAY("天", 86400, "yyyy-MM-dd"),
    WEEK("周", 604800, "yyyy-MM-dd"),
    MONTH("月", 25920000, "yyyy-MM"),
    YEAR("年", 31536000, "yyyy");

    TimeIntervalType(String name, int value, String timeFormat) {
        this.name = name;
        this.value = value;
        this.timeFormat = timeFormat;
    }

    private String name;
    private int value;

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    private String timeFormat;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    /**
     * 获取统计时间区间。获取区间时间判断标准为 >=start <end
     *
     * @param intervalType
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<TimePeriod> calcTimePeriod(TimeIntervalType intervalType, Long startTime, Long endTime) {
        if (endTime == null || endTime <= 0) {
            endTime = DateUtils.getTS();
        }

        Date startDate = DateUtils.ts2D(startTime);

        Long minuteStart = DateUtils.getMinuteStart(startDate).getTime() / 1000;
        Long hourStart = DateUtils.getHourStart(startDate).getTime() / 1000;
        Long dayStart = DateUtils.getDayStart(startDate).getTime() / 1000;
        Long weekStart = DateUtils.getWeekStart(false, startDate).getTime() / 1000;
        Long monthStart = DateUtils.getMonthStart(startDate).getTime() / 1000;
        Long yearStart = DateUtils.getYearStart(startDate).getTime() / 1000;

        startTime = intervalType.equals(TimeIntervalType.MINUTE) ?
                minuteStart : intervalType.equals(TimeIntervalType.HOUR) ?
                hourStart : intervalType.equals(TimeIntervalType.WEEK) ?
                weekStart : intervalType.equals(TimeIntervalType.MONTH) ?
                monthStart : intervalType.equals(TimeIntervalType.YEAR) ?
                yearStart : dayStart;

        List<TimePeriod> timeList = new ArrayList<>();

        Long stepEndtime = startTime;
        endTime = startTime > endTime ? (startTime + 1) : endTime;

        while (stepEndtime < endTime) {
            TimePeriod timePeriod = new TimePeriod();
            timePeriod.setStartTime(stepEndtime);

            if (intervalType.getValue() <= TimeIntervalType.WEEK.getValue()) {
                stepEndtime += intervalType.getValue();
            } else if (intervalType.equals(TimeIntervalType.MONTH)) {
                stepEndtime = DateUtils.getMonthEnd(DateUtils.ts2D(stepEndtime)).getTime() / 1000 + 1;
            } else if (intervalType.equals(TimeIntervalType.YEAR)) {
                stepEndtime = DateUtils.getYearEnd(DateUtils.ts2D(stepEndtime)).getTime() / 1000 + 1;
            }

            timePeriod.setEndTime(stepEndtime);

            timePeriod.setName(!intervalType.equals(TimeIntervalType.WEEK) ?
                    DateUtils.ts2S(timePeriod.getStartTime(), intervalType.getTimeFormat()) :
                    String.format("%s年第%s周", DateUtils.getYear(DateUtils.ts2D(timePeriod.getStartTime())), DateUtils.weekOfYear(DateUtils.ts2D(timePeriod.getStartTime()), false))
            );

            timeList.add(timePeriod);
        }
        return timeList;
    }

    /**
     * 根据时间获取周期的结束时间
     *
     * @param day
     * @param intervalType
     * @return
     */
    public Long timePeriodEnd(Long day, TimeIntervalType intervalType) {
        Date dayDate = DateUtils.ts2D(day);
        return (intervalType.equals(MINUTE) ? DateUtils.getMinuteEnd(dayDate) :
                intervalType.equals(HOUR) ? DateUtils.getHourEnd(dayDate) :
                        intervalType.equals(day) ? DateUtils.getDayEnd(dayDate) :
                                intervalType.equals(WEEK) ? DateUtils.getWeekEnd(false, dayDate) :
                                        intervalType.equals(MONTH) ? DateUtils.getMonthEnd(dayDate) : DateUtils.getYearEnd(dayDate)).
                getTime() / 1000;
    }

    /**
     * 根据时间获取周期的开始时间
     *
     * @param day
     * @param intervalType
     * @return
     */
    public Long timePeriodStart(Long day, TimeIntervalType intervalType) {
        Date dayDate = DateUtils.ts2D(day);
        return (intervalType.equals(MINUTE) ? DateUtils.getMinuteStart(dayDate) :
                intervalType.equals(HOUR) ? DateUtils.getHourStart(dayDate) :
                        intervalType.equals(day) ? DateUtils.getDayStart(dayDate) :
                                intervalType.equals(WEEK) ? DateUtils.getWeekStart(false, dayDate) :
                                        intervalType.equals(MONTH) ? DateUtils.getMonthStart(dayDate) : DateUtils.getYearStart(dayDate)).
                getTime() / 1000;
    }

    /**
     * 时间区间（周期）
     */
    @Data
    public static class TimePeriod {
        /**
         * 对应时间区间友好显示名称
         */
        private String name;

        /**
         * 开始时间戳
         */
        private Long startTime;

        /**
         * 结束时间戳
         */
        private Long endTime;

        /**
         * 统计数据
         */
        private Object data = 0;

        @Getter
        private String dateName;

        public String getDateName() {
            return this.startTime != null ? DateUtils.ts2S(this.startTime, "yyyy-MM-dd") : null;
        }

        @Getter
        private String weekName;

        public String getWeekName() {
            return this.startTime != null ? DateUtils.weekName(DateUtils.ts2D(this.startTime)) : null;
        }

        @Getter
        private String timeName;

        public String getTimeName() {
            return this.startTime != null ? DateUtils.ts2S(this.startTime, "yyyy-MM-dd HH:mm") : null;
        }
    }
}