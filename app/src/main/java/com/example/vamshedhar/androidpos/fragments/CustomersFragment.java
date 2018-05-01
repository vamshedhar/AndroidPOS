package com.example.vamshedhar.androidpos.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.QuickContactBadge;
import android.widget.Toast;

import com.example.vamshedhar.androidpos.R;
import com.example.vamshedhar.androidpos.adapters.CustomersListAdapter;
import com.example.vamshedhar.androidpos.objects.Customer;
import com.example.vamshedhar.androidpos.objects.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static android.widget.Toast.LENGTH_SHORT;


public class CustomersFragment extends Fragment implements CustomersListAdapter.CustomersListInterface{

    HashMap<String, Customer> customersMap;
    private RecyclerView customersList;
    private RecyclerView.Adapter customersListAdapter;
    private RecyclerView.LayoutManager customersListLayoutManager;
    private ImageView addCustomerBtn;
    private ProgressBar loader;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private DatabaseReference customersReference;
    private String username;


    public CustomersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customers, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        username = currentUser.getEmail().substring(0, currentUser.getEmail().indexOf('@'));

        databaseReference = FirebaseDatabase.getInstance().getReference().child(username);
        customersReference = databaseReference.child("customers");

        customersList = getView().findViewById(R.id.customersList);
        addCustomerBtn = getView().findViewById(R.id.addCustomerBtn);
        loader = getView().findViewById(R.id.loader);

        customersListLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        customersList.setLayoutManager(customersListLayoutManager);

        fetchCustomers();

        addCustomerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddCustomer();
            }
        });

    }

    public boolean isValidCustomer(String customerName, String customerPhone){
        if (customerName.isEmpty()){
            return false;
        }

        try {
            Double.parseDouble(customerPhone);
        } catch (Exception e){
            return false;
        }

        return true;
    }

    private void updateCustomer(String id, String newName, String newPhone){

        if (!isValidCustomer(newName, newPhone)){
            Toast.makeText(getActivity(), "Please Enter Valid Customer Details!", LENGTH_SHORT).show();
            return;
        }

        Customer customer = customersMap.get(id);
        customer.setName(newName);
        customer.setPhone_no(newPhone);

        customersReference.child(id).setValue(customer);
    }

    private void deleteCustomer(String id){
        customersReference.child(id).removeValue();
    }

    public void onAddCustomer(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View textEntryView = inflater.inflate(R.layout.add_customer, null);

        final EditText customerNameET = textEntryView.findViewById(R.id.customerName);
        final EditText customerPhoneET = textEntryView.findViewById(R.id.customerPhone);
        final EditText customerEmailET = textEntryView.findViewById(R.id.customerEmail);


        final android.app.AlertDialog.Builder addItemAlert = new android.app.AlertDialog.Builder(getActivity());

        addItemAlert.setTitle("Add New Customer:").setView(textEntryView).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addCustomer(customerNameET.getText().toString().trim(), customerPhoneET.getText().toString().trim(), customerEmailET.getText().toString().trim());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Nothing happens here
            }
        });

        addItemAlert.show();
    }

    public void addCustomer(String customerName, String customerPhone, String customerEmail){

        if (!isValidCustomer(customerName, customerPhone)){
            Toast.makeText(getActivity(), "Please Enter Valid Customer Details!", LENGTH_SHORT).show();
            return;
        }

        String id = customersReference.push().getKey();

        Customer customer = new Customer(id, customerName, customerEmail, username, customerPhone);

        customersReference.child(id).setValue(customer);

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
            Log.d("loadItems: ", customers.size()+"");
            customersListAdapter = new CustomersListAdapter(customers, getActivity(), this);
            customersList.setAdapter(customersListAdapter);
        }
    }

    @Override
    public void onCustomerUpdate(String id) {

        final Customer customer = customersMap.get(id);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View textEntryView = inflater.inflate(R.layout.edit_customer, null);

        final EditText customerNameET = textEntryView.findViewById(R.id.customerName);
        final EditText customerPhoneET = textEntryView.findViewById(R.id.customerPhone);

        customerNameET.setText(customer.getName());
        customerPhoneET.setText(customer.getPhone_no() + "");

        customerNameET.setTag(customer.getId());

        final AlertDialog.Builder editCustomerAlert = new AlertDialog.Builder(getActivity());

        editCustomerAlert.setTitle("Update Item:").setView(textEntryView).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //update customer
                updateCustomer(customer.getId(), customerNameET.getText().toString().trim(), customerPhoneET.getText().toString().trim());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Nothing happens here
            }
        });

        editCustomerAlert.show();

    }

    @Override
    public void onItemLongClick(String id) {

        AlertDialog.Builder deleteItemDiag = new AlertDialog.Builder(getActivity());

        final Customer customer = customersMap.get(id);

        deleteItemDiag.setTitle("Are you sure you want to delete " + customer.getName() + "?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //delete customer
                        deleteCustomer(customer.getId());
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        deleteItemDiag.show();

    }
}
