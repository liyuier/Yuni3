package com.yuier.yuni.core.util;

/**
 * Cron表达式生成工具类（构建器模式-强类型）
 * 支持链式调用和流畅API，参数类型安全
 */
public class CronExpressionBuilder {

    private String seconds = "*";
    private String minutes = "*";
    private String hours = "*";
    private String dayOfMonth = "*";
    private String month = "*";
    private String dayOfWeek = "?";
    private String year = ""; // 可选字段

    /**
     * 私有构造函数，强制使用静态工厂方法
     */
    private CronExpressionBuilder() {}

    /**
     * 静态工厂方法，创建新的构建器实例
     */
    public static CronExpressionBuilder create() {
        return new CronExpressionBuilder();
    }

    /**
     * 设置秒字段
     */
    public CronExpressionBuilder seconds(String seconds) {
        this.seconds = seconds;
        return this;
    }

    /**
     * 设置秒字段（数字）
     */
    public CronExpressionBuilder seconds(int seconds) {
        validateRange(seconds, 0, 59, "seconds");
        this.seconds = String.valueOf(seconds);
        return this;
    }

    /**
     * 设置分钟字段
     */
    public CronExpressionBuilder minutes(String minutes) {
        this.minutes = minutes;
        return this;
    }

    /**
     * 设置分钟字段（数字）
     */
    public CronExpressionBuilder minutes(int minutes) {
        validateRange(minutes, 0, 59, "minutes");
        this.minutes = String.valueOf(minutes);
        return this;
    }

    /**
     * 设置小时字段
     */
    public CronExpressionBuilder hours(String hours) {
        this.hours = hours;
        return this;
    }

    /**
     * 设置小时字段（数字）
     */
    public CronExpressionBuilder hours(int hours) {
        validateRange(hours, 0, 23, "hours");
        this.hours = String.valueOf(hours);
        return this;
    }

    /**
     * 设置日期字段（每月的第几天）
     */
    public CronExpressionBuilder dayOfMonth(String dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
        // 如果设置了日期，通常需要将星期字段设为 ?，避免冲突
        if (!"*".equals(dayOfMonth) && !"?".equals(dayOfMonth)) {
            this.dayOfWeek = "?";
        }
        return this;
    }

    /**
     * 设置日期字段（数字）
     */
    public CronExpressionBuilder dayOfMonth(int dayOfMonth) {
        validateRange(dayOfMonth, 1, 31, "dayOfMonth");
        this.dayOfMonth = String.valueOf(dayOfMonth);
        this.dayOfWeek = "?";
        return this;
    }

    /**
     * 设置月份字段
     */
    public CronExpressionBuilder month(String month) {
        this.month = month;
        return this;
    }

    /**
     * 设置月份字段（数字）
     */
    public CronExpressionBuilder month(int month) {
        validateRange(month, 1, 12, "month");
        this.month = String.valueOf(month);
        return this;
    }

    /**
     * 设置星期字段（每周的第几天）
     */
    public CronExpressionBuilder dayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        // 如果设置了星期，通常需要将日期字段设为 ?，避免冲突
        if (!"*".equals(dayOfWeek) && !"?".equals(dayOfWeek)) {
            this.dayOfMonth = "?";
        }
        return this;
    }

    /**
     * 设置星期字段（数字，1=周日，2=周一，...，7=周六）
     */
    public CronExpressionBuilder dayOfWeek(int dayOfWeek) {
        validateRange(dayOfWeek, 1, 7, "dayOfWeek");
        this.dayOfWeek = String.valueOf(dayOfWeek);
        this.dayOfMonth = "?";
        return this;
    }

    /**
     * 设置年份字段
     */
    public CronExpressionBuilder year(String year) {
        this.year = year;
        return this;
    }

    /**
     * 设置年份字段（数字）
     */
    public CronExpressionBuilder year(int year) {
        this.year = String.valueOf(year);
        return this;
    }

    /**
     * 每秒执行
     */
    public CronExpressionBuilder everySecond() {
        return seconds("*");
    }

    /**
     * 每隔几秒执行
     * @param interval 间隔秒数
     */
    public CronExpressionBuilder everySeconds(int interval) {
        validateRange(interval, 1, 59, "interval");
        return seconds("0/" + interval);
    }

    /**
     * 每分钟执行
     */
    public CronExpressionBuilder everyMinute() {
        return minutes("*");
    }

    /**
     * 每隔几分钟执行
     * @param interval 间隔分钟数
     */
    public CronExpressionBuilder everyMinutes(int interval) {
        validateRange(interval, 1, 59, "interval");
        return minutes("0/" + interval);
    }

    /**
     * 每小时执行
     */
    public CronExpressionBuilder everyHour() {
        return hours("*");
    }

    /**
     * 每隔几小时执行
     * @param interval 间隔小时数
     */
    public CronExpressionBuilder everyHours(int interval) {
        validateRange(interval, 1, 23, "interval");
        return hours("0/" + interval);
    }

    /**
     * 每天指定时间执行
     * @param hour 小时（24小时制）
     * @param minute 分钟
     */
    public CronExpressionBuilder dailyAt(int hour, int minute) {
        return hours(hour)
                .minutes(minute)
                .seconds(0);
    }

    /**
     * 每天凌晨执行
     */
    public CronExpressionBuilder dailyAtMidnight() {
        return dailyAt(0, 0);
    }

    /**
     * 每天中午执行
     */
    public CronExpressionBuilder dailyAtNoon() {
        return dailyAt(12, 0);
    }

    /**
     * 每周指定星期几执行
     * @param dayOfWeek 星期几（1=周日，2=周一，...，7=周六）
     */
    public CronExpressionBuilder weeklyOn(int dayOfWeek) {
        return dayOfWeek(dayOfWeek)
                .hours(0)
                .minutes(0)
                .seconds(0);
    }

    /**
     * 每周日凌晨执行
     */
    public CronExpressionBuilder weeklyOnSunday() {
        return weeklyOn(1);
    }

    /**
     * 每周一下午3点执行
     */
    public CronExpressionBuilder weeklyMondayAt3PM() {
        return dayOfWeek(2)
                .hours(15)
                .minutes(0)
                .seconds(0);
    }

    /**
     * 每月指定日期执行
     * @param day 日期（1-31）
     */
    public CronExpressionBuilder monthlyOn(int day) {
        return dayOfMonth(day)
                .hours(0)
                .minutes(0)
                .seconds(0);
    }

    /**
     * 每月最后一天执行
     */
    public CronExpressionBuilder monthlyLastDay() {
        return dayOfMonth("L");
    }

    /**
     * 每个工作日执行（周一到周五）
     */
    public CronExpressionBuilder weekdays() {
        return dayOfWeek("2-6");
    }

    /**
     * 每月第一个工作日执行
     */
    public CronExpressionBuilder firstWeekdayOfMonth() {
        return dayOfMonth("1W");
    }

    /**
     * 每月最后一个工作日执行
     */
    public CronExpressionBuilder lastWeekdayOfMonth() {
        return dayOfMonth("LW");
    }

    /**
     * 验证数值范围
     */
    private void validateRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                    String.format("%s must be between %d and %d, but was %d",
                            fieldName, min, max, value));
        }
    }

    /**
     * 构建最终的cron表达式
     */
    public String build() {
        StringBuilder cron = new StringBuilder();
        cron.append(seconds).append(" ")
                .append(minutes).append(" ")
                .append(hours).append(" ")
                .append(dayOfMonth).append(" ")
                .append(month).append(" ")
                .append(dayOfWeek);

        if (!year.isEmpty()) {
            cron.append(" ").append(year);
        }

        return cron.toString();
    }

    /**
     * 返回cron表达式字符串（调用build方法）
     */
    @Override
    public String toString() {
        return build();
    }

    // 测试和演示
    private static void example(String[] args) {
        System.out.println("=== Cron表达式生成示例（构建器模式-强类型） ===");

        // 每2秒执行一次
        System.out.println("每2秒: " + CronExpressionBuilder.create().everySeconds(2).build());

        // 每5分钟执行一次
        System.out.println("每5分钟: " + CronExpressionBuilder.create().everyMinutes(5).build());

        // 每小时执行一次
        System.out.println("每小时: " + CronExpressionBuilder.create().everyHours(1).build());

        // 每天凌晨2点执行
        System.out.println("每天2点: " + CronExpressionBuilder.create().dailyAt(2, 0).build());

        // 每天上午9点执行
        System.out.println("每天9点: " + CronExpressionBuilder.create().dailyAt(9, 0).build());

        // 每个工作日执行
        System.out.println("工作日: " + CronExpressionBuilder.create().weekdays().build());

        // 每周日凌晨执行
        System.out.println("每周日凌晨: " + CronExpressionBuilder.create().weeklyOn(1).build());

        // 每月1号执行
        System.out.println("每月1号: " + CronExpressionBuilder.create().monthlyOn(1).build());

        // 使用链式调用方式 - 每月15号上午10点
        System.out.println("自定义-每月15号上午10点: " +
                CronExpressionBuilder.create()
                        .monthlyOn(15)
                        .hours(10)
                        .minutes(0)
                        .seconds(0)
                        .build());

        // 每月最后一天执行
        System.out.println("每月最后一天: " +
                CronExpressionBuilder.create()
                        .monthlyLastDay()
                        .hours(23)
                        .minutes(59)
                        .seconds(59)
                        .build());

        // 复杂示例：每月第一个工作日上午9点
        System.out.println("每月第一个工作日上午9点: " +
                CronExpressionBuilder.create()
                        .firstWeekdayOfMonth()
                        .hours(9)
                        .minutes(0)
                        .seconds(0)
                        .build());

        // 每周一下午3点执行
        System.out.println("每周一下午3点: " +
                CronExpressionBuilder.create()
                        .weeklyMondayAt3PM()
                        .build());

        // 完全自定义表达式
        System.out.println("自定义-每月15号和月末，上午8点: " +
                CronExpressionBuilder.create()
                        .minutes(0)
                        .hours(8)
                        .dayOfMonth("15,L")
                        .month("*")
                        .dayOfWeek("?")
                        .build());

        // 错误示例（会抛出异常）
        try {
            CronExpressionBuilder.create().hours(25).build(); // 超出范围
        } catch (IllegalArgumentException e) {
            System.out.println("错误示例: " + e.getMessage());
        }
    }
}