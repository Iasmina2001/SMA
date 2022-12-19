package com.sma.lab10;

public enum Month {
    January, February, March, April, May, June, July, August, September, October, November, December;

    public static int monthNameToInt(Month month) {
        return month.ordinal();
    }

    public static String intToMonthName(int index) {
        return Month.values()[index].toString();
    }

    public static int monthFromTimestamp(String timestamp) {
        // 2016-11-02 14:15:16
        int month = Integer.parseInt(timestamp.substring(5, 7));
        return month - 1;
    }
}