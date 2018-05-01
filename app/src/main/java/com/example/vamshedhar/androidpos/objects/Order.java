package com.example.vamshedhar.androidpos.objects;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vamshedhar on 4/17/18.
 */

public class Order extends CreatedTimeObject implements Comparable<Order>, Serializable {
    private String id, created_user, customer_id;

    private ArrayList<OrderItem> items;
    private double base_value, tax, total_amount;

    public Order() {
        super();
        this.items = new ArrayList<>();
        this.base_value = 0;
        this.tax = 0;
        this.total_amount = 0;
    }

    public Order(String id, String created_user, String customer_id) {
        this.id = id;
        this.created_user = created_user;
        this.customer_id = customer_id;
        this.items = new ArrayList<>();
        this.base_value = 0;
        this.tax = 0;
        this.total_amount = 0;
    }

    public void setBaseValue(double base_value) {
        this.base_value = base_value;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public void setTotalAmount(double total_amount) {
        this.total_amount = total_amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedUser() {
        return created_user;
    }

    public void setCreatedUser(String created_user) {
        this.created_user = created_user;
    }

    public String getCustomerId() {
        return customer_id;
    }

    public void setCustomerId(String customer_id) {
        this.customer_id = customer_id;
    }

    public ArrayList<OrderItem> getItems() {
        return items;
    }

    public double getBaseValue() {
        return base_value;
    }

    public double getTax() {
        return tax;
    }

    public double getTotalAmount() {
        return total_amount;
    }

    public void addItem(OrderItem item){
        this.items.add(item);

        this.base_value += item.getAmount();
        double tax = item.getAmount() * Tax.tax_percent/100;

        this.tax += tax;

        this.total_amount = this.base_value + this.tax;
    }

    @Override
    public int compareTo(@NonNull Order order) {
        if (this.getCreateTimestamp() < order.getCreateTimestamp()){
            return 1;
        } else if (this.getCreateTimestamp() > order.getCreateTimestamp()){
            return -1;
        }

        return 0;
    }
}
