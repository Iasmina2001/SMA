package com.sma.lab9.ui;

import java.util.Objects;

public class Payment {

    private double cost;
    private String name;
    private String type;
    private String timestamp;

    public Payment() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Payment(double cost, String name, String type, String timestamp) {
        this.cost = cost;
        this.name = name;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public String getType() {
        return type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Double.compare(payment.cost, cost) == 0 && Objects.equals(name, payment.name) && Objects.equals(type, payment.type) && Objects.equals(timestamp, payment.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cost, name, type, timestamp);
    }
}