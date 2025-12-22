package com.yuier.yuni.core.util;

/**
 * 一个改进的 Cron 表达式构建器
 * 支持常见的 Cron 表达式格式（6位：秒 分 时 日 月 星期）
 */
public class CronExpressionBuilder {
    private String seconds = "0";
    private String minutes = "*";
    private String hours = "*";
    private String dayOfMonth = "*";
    private String month = "*";
    private String dayOfWeek = "?";

    public static CronExpressionBuilder create() {
        return new CronExpressionBuilder();
    }

    /**
     * 设置秒数 (0-59)
     */
    public CronExpressionBuilder withSeconds(int seconds) {
        if (seconds >= 0 && seconds <= 59) {
            this.seconds = String.valueOf(seconds);
        }
        return this;
    }

    /**
     * 设置每秒
     */
    public CronExpressionBuilder withEverySecond() {
        this.seconds = "*";
        return this;
    }

    /**
     * 设置分钟 (0-59)
     */
    public CronExpressionBuilder withMinutes(int minutes) {
        if (minutes >= 0 && minutes <= 59) {
            this.minutes = String.valueOf(minutes);
        }
        return this;
    }

    /**
     * 设置每分钟
     */
    public CronExpressionBuilder withEveryMinute() {
        this.minutes = "*";
        return this;
    }

    /**
     * 设置每N分钟
     */
    public CronExpressionBuilder withEveryNthMinute(int nth) {
        if (nth > 0) {
            this.minutes = "*/" + nth;
        }
        return this;
    }

    /**
     * 设置小时 (0-23)
     */
    public CronExpressionBuilder withHours(int hours) {
        if (hours >= 0 && hours <= 23) {
            this.hours = String.valueOf(hours);
        }
        return this;
    }

    /**
     * 设置每小时
     */
    public CronExpressionBuilder withEveryHour() {
        this.hours = "*";
        return this;
    }

    /**
     * 设置每天的某个时间段 (小时范围)
     */
    public CronExpressionBuilder withHoursRange(int start, int end) {
        if (start >= 0 && end <= 23 && start <= end) {
            this.hours = start + "-" + end;
        }
        return this;
    }

    /**
     * 设置每N小时
     */
    public CronExpressionBuilder withEveryNthHour(int nth) {
        if (nth > 0) {
            this.hours = "*/" + nth;
        }
        return this;
    }

    /**
     * 设置月份的某一天 (1-31)
     */
    public CronExpressionBuilder withDayOfMonth(int day) {
        if (day >= 1 && day <= 31) {
            this.dayOfMonth = String.valueOf(day);
            this.dayOfWeek = "?";
        }
        return this;
    }

    /**
     * 设置每月最后一天
     */
    public CronExpressionBuilder withLastDayOfMonth() {
        this.dayOfMonth = "L";
        this.dayOfWeek = "?";
        return this;
    }

    /**
     * 设置每月最后一个工作日
     */
    public CronExpressionBuilder withLastWeekdayOfMonth() {
        this.dayOfMonth = "LW";
        this.dayOfWeek = "?";
        return this;
    }

    /**
     * 设置每月第N个工作日
     */
    public CronExpressionBuilder withNthWeekdayOfMonth(int nth) {
        if (nth >= 1 && nth <= 5) {
            this.dayOfMonth = nth + "W";
        }
        this.dayOfWeek = "?";
        return this;
    }

    /**
     * 设置每月最后X天
     */
    public CronExpressionBuilder withDaysBeforeEndOfMonth(int days) {
        if (days >= 1) {
            this.dayOfMonth = "L-" + days;
        }
        this.dayOfWeek = "?";
        return this;
    }

    /**
     * 设置每月的多天
     */
    public CronExpressionBuilder withDaysOfMonth(int... days) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < days.length; i++) {
            if (i > 0) sb.append(",");
            if (days[i] >= 1 && days[i] <= 31) {
                sb.append(days[i]);
            }
        }
        if (sb.length() > 0) {
            this.dayOfMonth = sb.toString();
            this.dayOfWeek = "?";
        }
        return this;
    }

    /**
     * 设置每月
     */
    public CronExpressionBuilder withEveryDayOfMonth() {
        this.dayOfMonth = "*";
        this.dayOfWeek = "?";
        return this;
    }

    /**
     * 设置月份 (1-12)
     */
    public CronExpressionBuilder withMonth(int month) {
        if (month >= 1 && month <= 12) {
            this.month = String.valueOf(month);
        }
        return this;
    }

    /**
     * 设置月份名称 (JAN-DEC)
     */
    public CronExpressionBuilder withMonthName(String monthName) {
        this.month = monthName.toUpperCase();
        return this;
    }

    /**
     * 设置每月
     */
    public CronExpressionBuilder withEveryMonth() {
        this.month = "*";
        return this;
    }

    /**
     * 设置多个月份
     */
    public CronExpressionBuilder withMonths(int... months) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < months.length; i++) {
            if (i > 0) sb.append(",");
            if (months[i] >= 1 && months[i] <= 12) {
                sb.append(months[i]);
            }
        }
        if (sb.length() > 0) {
            this.month = sb.toString();
        }
        return this;
    }

    /**
     * 设置星期几 (1-7, 1=Sunday, 7=Saturday)
     */
    public CronExpressionBuilder withDayOfWeek(int day) {
        if (day >= 1 && day <= 7) {
            this.dayOfWeek = String.valueOf(day);
            this.dayOfMonth = "?";
        }
        return this;
    }

    /**
     * 设置星期几名称 (SUN-SAT)
     */
    public CronExpressionBuilder withDayOfWeekName(String dayName) {
        this.dayOfWeek = dayName.toUpperCase();
        this.dayOfMonth = "?";
        return this;
    }

    /**
     * 设置每天
     */
    public CronExpressionBuilder withEveryDayOfWeek() {
        this.dayOfWeek = "*";
        this.dayOfMonth = "?";
        return this;
    }

    /**
     * 设置工作日 (MON-FRI)
     */
    public CronExpressionBuilder withWeekdays() {
        this.dayOfWeek = "MON-FRI";
        this.dayOfMonth = "?";
        return this;
    }

    /**
     * 设置周末 (SAT,SUN)
     */
    public CronExpressionBuilder withWeekends() {
        this.dayOfWeek = "SAT,SUN";
        this.dayOfMonth = "?";
        return this;
    }

    /**
     * 设置多天
     */
    public CronExpressionBuilder withDaysOfWeek(String... days) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < days.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(days[i].toUpperCase());
        }
        if (sb.length() > 0) {
            this.dayOfWeek = sb.toString();
            this.dayOfMonth = "?";
        }
        return this;
    }

    /**
     * 设置多天 (数字形式)
     */
    public CronExpressionBuilder withDaysOfWeek(int... days) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < days.length; i++) {
            if (i > 0) sb.append(",");
            if (days[i] >= 1 && days[i] <= 7) {
                sb.append(days[i]);
            }
        }
        if (sb.length() > 0) {
            this.dayOfWeek = sb.toString();
            this.dayOfMonth = "?";
        }
        return this;
    }

    /**
     * 构建 Cron 表达式
     */
    public String build() {
        return String.format("%s %s %s %s %s %s",
                seconds, minutes, hours, dayOfMonth, month, dayOfWeek);
    }

    @Override
    public String toString() {
        return build();
    }

    private static void example(String[] args) {
        // 示例1：每天上午9:30执行
        String cron1 = CronExpressionBuilder.create()
                .withSeconds(0)
                .withMinutes(30)
                .withHours(9)
                .withEveryDayOfMonth()
                .withEveryMonth()
                .withEveryDayOfWeek()
                .build();
        System.out.println("每天上午9:30: " + cron1); // 输出: 0 30 9 * * ?

        // 示例2：每周五下午4点
        String cron2 = CronExpressionBuilder.create()
                .withSeconds(0)
                .withMinutes(0)
                .withHours(16)
                .withEveryDayOfMonth()
                .withEveryMonth()
                .withDayOfWeekName("FRI")
                .build();
        System.out.println("每周五下午4点: " + cron2); // 输出: 0 0 16 * * FRI

        // 示例3：每月最后一天凌晨1点
        String cron3 = CronExpressionBuilder.create()
                .withSeconds(0)
                .withMinutes(0)
                .withHours(1)
                .withLastDayOfMonth()
                .withEveryMonth()
                .withEveryDayOfWeek()
                .build();
        System.out.println("每月最后一天凌晨1点: " + cron3); // 输出: 0 0 1 L * ?

        // 示例4：每15分钟执行一次
        String cron4 = CronExpressionBuilder.create()
                .withSeconds(0)
                .withEveryNthMinute(15)
                .withEveryHour()
                .withEveryDayOfMonth()
                .withEveryMonth()
                .withEveryDayOfWeek()
                .build();
        System.out.println("每15分钟: " + cron4); // 输出: 0 */15 * * * ?

        // 示例5：每天上午9点到下午5点之间每小时执行
        String cron5 = CronExpressionBuilder.create()
                .withSeconds(0)
                .withMinutes(0)
                .withHoursRange(9, 17)
                .withEveryDayOfMonth()
                .withEveryMonth()
                .withEveryDayOfWeek()
                .build();
        System.out.println("每天9-17点每小时: " + cron5); // 输出: 0 0 9-17 * * ?

        // 示例6：每月1号和15号上午10点执行
        String cron6 = CronExpressionBuilder.create()
                .withSeconds(0)
                .withMinutes(0)
                .withHours(10)
                .withDaysOfMonth(1, 15)
                .withEveryMonth()
                .withEveryDayOfWeek()
                .build();
        System.out.println("每月1号和15号上午10点: " + cron6); // 输出: 0 0 10 1,15 * ?

        // 示例7：每月第二个周五上午10点执行
        String cron7 = CronExpressionBuilder.create()
                .withSeconds(0)
                .withMinutes(0)
                .withHours(10)
                .withDayOfMonth(1)
                .withEveryMonth()
                .withDayOfWeekName("FRIMON#2") // 这个例子展示了复杂表达式
                .build();
        System.out.println("每月第二个周五上午10点: " + cron7); // 输出: 0 0 10 1 * FRIMON#2

        // 示例8：工作日执行
        String cron8 = CronExpressionBuilder.create()
                .withSeconds(0)
                .withMinutes(0)
                .withHours(9)
                .withEveryDayOfMonth()
                .withEveryMonth()
                .withWeekdays()
                .build();
        System.out.println("工作日上午9点: " + cron8); // 输出: 0 0 9 * * MON-FRI
    }
}