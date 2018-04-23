package com.example.vamshedhar.androidpos.objects;

import android.support.annotation.NonNull;

/**
 * Created by vamshedhar on 4/17/18.
 */

public class Item extends CreatedTimeObject implements Comparable<Item> {

    private String id, name, created_user;
    private double price;

    public Item() {
    }

    public Item(String id, String name, String created_user, double price) {
        super();
        this.id = id;
        this.name = name;
        this.created_user = created_user;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedUser() {
        return created_user;
    }

    public void setCreatedUser(String created_user) {
        this.created_user = created_user;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }

    @Override
    public int compareTo(@NonNull Item item) {
        return this.getName().compareTo(item.getName());
    }
}
