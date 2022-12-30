package com.sma.lab11.ui;

import android.graphics.Color;

public class PaymentType {
    public static int getColorFromPaymentType(String type) {
        type = type.toLowerCase();
        switch (type) {
            case "entertainment":
                return Color.rgb(200, 50, 50);
            case "food":
                return Color.rgb(50, 150, 50);
            case "taxes":
                return Color.rgb(20, 20, 150);
            case "travel":
                return Color.rgb(230, 140, 0);
            default:
                return Color.rgb(100, 100, 100);
        }
    }

    public static String[] getTypes() {
        return new String[]{"entertainment", "food", "taxes", "travel", "other"};
    }
}