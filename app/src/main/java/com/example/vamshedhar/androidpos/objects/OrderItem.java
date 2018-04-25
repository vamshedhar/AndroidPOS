package com.example.vamshedhar.androidpos.objects;

import java.io.Serializable;

/**
 * Created by vamshedhar on 4/17/18.
 */

public class OrderItem implements Serializable {
    private String item_id;
    private double quantity, amount;

    public OrderItem() {
    }

    public OrderItem(String item_id, double quantity, double amount) {
        this.item_id = item_id;
        this.quantity = quantity;
        this.amount = amount;
    }

    public String getItemId() {
        return item_id;
    }

    public void setItemId(String item_id) {
        this.item_id = item_id;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

}
