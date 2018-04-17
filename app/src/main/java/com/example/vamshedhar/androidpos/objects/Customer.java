package com.example.vamshedhar.androidpos.objects;

import android.support.annotation.NonNull;

/**
 * Created by vamshedhar on 4/17/18.
 */

public class Customer extends CreatedTimeObject implements Comparable<Customer> {

    private String id, name, email, created_user;

    public Customer() {

    }

    public Customer(String id, String name, String email, String created_user) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.created_user = created_user;
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
        return "Customer{" + "name='" + name + '\'' + ", email='" + email + '\'' + '}';
    }

    @Override
    public int compareTo(@NonNull Customer customer) {
        return this.getName().compareTo(customer.getName());
    }
}
