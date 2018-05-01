package com.example.vamshedhar.androidpos.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.vamshedhar.androidpos.R;
import com.example.vamshedhar.androidpos.adapters.CustomersListAdapter;
import com.example.vamshedhar.androidpos.fragments.SellFragment;
import com.example.vamshedhar.androidpos.objects.Customer;
import com.example.vamshedhar.androidpos.objects.Item;
import com.example.vamshedhar.androidpos.objects.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class AddCustomerActivity extends AppCompatActivity implements CustomersListAdapter.CustomersListInterface{

    private RecyclerView customersList;
    private RecyclerView.Adapter customerListAdapter;
    private RecyclerView.LayoutManager customersListLayoutManager;
    private ProgressBar loader;

    private ImageView add_customer;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private DatabaseReference customersReference;
    private String username;
    HashMap<String, Customer> customersMap;
    private RecyclerView.Adapter customersListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        add_customer = findViewById(R.id.addCustomer);
        customersList = findViewById(R.id.customersList);
        loader = findViewById(R.id.loader);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        customersReference = databaseReference.child("customers");

        username = currentUser.getEmail().substring(0, currentUser.getEmail().indexOf('@'));

        customersListLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        customersList.setLayoutManager(customersListLayoutManager);

        add_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(AddCustomerActivity.this);
                final View textEntryView = inflater.inflate(R.layout.add_customer, null);

                final EditText customerNameET = textEntryView.findViewById(R.id.customerName);
                final EditText customerNumberET = textEntryView.findViewById(R.id.customerPhone);
                final EditText customerEmailET = textEntryView.findViewById(R.id.customerEmail);


                final AlertDialog.Builder addcustomer = new AlertDialog.Builder(AddCustomerActivity.this);

                addcustomer.setTitle("Add Customer:").setView(textEntryView).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addCustomer(customerNameET.getText().toString().trim(), customerNumberET.getText().toString().trim(), customerEmailET.getText().toString().trim());
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Nothing happens here
                    }
                });

                addcustomer.show();
            }
        });


        fetchCustomers();
    }


    public void fetchCustomers(){
        loader.setVisibility(View.VISIBLE);
        customersList.setVisibility(View.INVISIBLE);
        customersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                customersMap = new HashMap<>();

                for (DataSnapshot customerSnap : dataSnapshot.getChildren()){
                    Customer customer = customerSnap.getValue(Customer.class);
                    customersMap.put(customer.getId(), customer);
                }

                loadItems();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadItems(){
        loader.setVisibility(View.INVISIBLE);
        customersList.setVisibility(View.VISIBLE);
        if (customersMap != null){
            ArrayList<Customer> customers = new ArrayList<>(customersMap.values());
            Collections.sort(customers);
            Log.d("loadItems: ", customers.size()+"");
            customersListAdapter = new CustomersListAdapter(customers, AddCustomerActivity.this, this);
            customersList.setAdapter(customersListAdapter);
        }
    }

    public boolean isValidCustomer(String name, String phoneNumber){
        if (name.isEmpty()){
            return false;
        }

        if (phoneNumber.isEmpty() || !Patterns.PHONE.matcher(phoneNumber).matches()){
            return false;
        }

        return true;
    }

    public void addCustomer(String name, String phoneNumber, String email){
        if (!isValidCustomer(name, phoneNumber)){
            return;
        }

        String id = customersReference.push().getKey();
        Customer customer = new Customer(id, name, email, username, phoneNumber);

        customersReference.child(id).setValue(customer);

        Intent intent = new Intent();
        intent.putExtra(SellFragment.CUSTOMER_DETAILS, customer);
        setResult(RESULT_OK, intent);
        finish();

    }

    @Override
    public void onCustomerUpdate(String id) {

        final Customer customer = customersMap.get(id);

        Intent intent = new Intent();
        intent.putExtra(SellFragment.CUSTOMER_DETAILS, customer);
        setResult(RESULT_OK, intent);
        finish();


    }

    @Override
    public void onItemLongClick(String id) {

    }
}
