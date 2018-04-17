package com.example.vamshedhar.androidpos.objects;

/**
 * Created by vamshedhar on 4/17/18.
 */

public class OrderItem {
    private String item_id;
    private float quantity, amount;

    public OrderItem() {
    }

    public OrderItem(String item_id, float quantity, float amount) {
        this.item_id = item_id;
        this.quantity = quantity;
        this.amount = amount;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

}
