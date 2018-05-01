package com.example.vamshedhar.androidpos.objects;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by vamshedhar on 4/17/18.
 */

public class Customer extends CreatedTimeObject implements Comparable<Customer>, Serializable {

    private String id;
    private String name;
    private String email;
    private String created_user;
    private String phone_no;
    private HashMap<String, Object> last_order;
    private double total_order_amount;
    private int total_orders;

    public Customer(String id, String name, String email, String created_user, String phone_no) {
        super();
        this.id = id;
        this.name = name;
        this.email = email;
        this.created_user = created_user;
        this.phone_no = phone_no;
        this.total_order_amount = 0;
    }

    public int getTotalOrders() {
        return total_orders;
    }

    public void setTotalOrders(int total_orders) {
        this.total_orders = total_orders;
    }

    public HashMap<String, Object> getLast_order() {
        return last_order;
    }

    public HashMap<String, Object> getLastOrder() {
        return last_order;
    }

    public void setLast_order(HashMap<String, Object> last_order) {
        this.last_order = last_order;
    }

    @Exclude
    public long getLastOrderTime() {
        if (last_order != null){
            return (long) last_order.get("timestamp");
        } else{
            return -1;
        }
    }
    public void setLastOrder(HashMap<String, Object> last_order) {
        this.last_order = last_order;
    }

    public double getTotalOrderAmount() {
        return total_order_amount;
    }

    public void setTotalOrderAmount(double total_order_amount) {
        this.total_order_amount = total_order_amount;
    }

    public Customer() {
        super();
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreatedUser() {
        return created_user;
    }

    public void setCreatedUser(String created_user) {
        this.created_user = created_user;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone_no='" + phone_no + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull Customer customer) {
        if (this.getTotalOrders() < customer.getTotalOrders()){
            return 1;
        } else if (this.getTotalOrders() > customer.getTotalOrders()){
            return -1;
        }

        return 0;
    }
}
