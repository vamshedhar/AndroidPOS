package com.example.vamshedhar.androidpos.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.vamshedhar.androidpos.R;
import com.example.vamshedhar.androidpos.fragments.SellFragment;
import com.example.vamshedhar.androidpos.objects.Customer;
import com.example.vamshedhar.androidpos.objects.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddCustomerActivity extends AppCompatActivity {

    private RecyclerView customersList;
    private RecyclerView.Adapter customerListAdapter;
    private RecyclerView.LayoutManager customersListLayoutManager;

    private ImageView addCustomer;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private DatabaseReference customersReference;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        addCustomer = findViewById(R.id.addCustomer);
        customersList = findViewById(R.id.customersList);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        customersReference = databaseReference.child("customers");

        username = currentUser.getEmail().substring(0, currentUser.getEmail().indexOf('@'));

        customersListLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        customersList.setLayoutManager(customersListLayoutManager);

        addCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(AddCustomerActivity.this);
                final View textEntryView = inflater.inflate(R.layout.add_customer, null);

                final EditText customerNameET = textEntryView.findViewById(R.id.customerName);
                final EditText customerNumberET = textEntryView.findViewById(R.id.customerNumber);


                final AlertDialog.Builder addCustomer = new AlertDialog.Builder(AddCustomerActivity.this);

                addCustomer.setTitle("Add Customer:").setView(textEntryView).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addCustomer(customerNameET.getText().toString().trim(), customerNumberET.getText().toString().trim());
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Nothing happens here
                    }
                });

                addCustomer.show();
            }
        });
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

    public void addCustomer(String name, String phoneNumber){
        if (!isValidCustomer(name, phoneNumber)){
            return;
        }

        String id = customersReference.push().getKey();
        Customer customer = new Customer(id, name, "", username, phoneNumber);

        customersReference.child(id).setValue(customer);

        Intent intent = new Intent();
        intent.putExtra(SellFragment.CUSTOMER_DETAILS, customer);
        setResult(RESULT_OK, intent);
        finish();

    }
}
