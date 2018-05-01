package com.example.vamshedhar.androidpos.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.vamshedhar.androidpos.R;
import com.example.vamshedhar.androidpos.activities.FinishOrderActivity;
import com.example.vamshedhar.androidpos.activities.MainActivity;
import com.example.vamshedhar.androidpos.adapters.ItemListAdapter;
import com.example.vamshedhar.androidpos.adapters.PastOrdersAdapter;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;


public class PastOrdersFragment extends Fragment implements PastOrdersAdapter.PastOrdersListInterface {

    HashMap<String, Customer> customersMap;
    HashMap<String, Order> ordersMap;
    ArrayList<Order> orders;

    public static String ORDER_DETAILS_KEY = "ORDER_DETAILS_KEY";

    private RecyclerView ordersList;
    private RecyclerView.Adapter orderListAdapter;
    private RecyclerView.LayoutManager ordersListLayoutManager;
    private ProgressBar loader;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private DatabaseReference ordersReference;
    private DatabaseReference customersReference;
    private String username;

    public PastOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_past_orders, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ordersList = getView().findViewById(R.id.pastOrdersList);
        loader= getView().findViewById(R.id.loader);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        customersReference = databaseReference.child("customers");
        ordersReference = databaseReference.child("orders");

        username = currentUser.getEmail().substring(0, currentUser.getEmail().indexOf('@'));

        ordersListLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        ordersList.setLayoutManager(ordersListLayoutManager);

        fetchCustomers();
    }


    public void fetchCustomers(){
        loader.setVisibility(View.VISIBLE);
        ordersList.setVisibility(View.INVISIBLE);
        customersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                customersMap = new HashMap<>();

                for (DataSnapshot itemSnap : dataSnapshot.getChildren()){
                    customersMap.put(itemSnap.getKey(), itemSnap.getValue(Customer.class));
                }

                fetchOrders();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void fetchOrders(){

        ordersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ordersMap = new HashMap<>();
                Calendar now = Calendar.getInstance();
                for (DataSnapshot itemSnap : dataSnapshot.getChildren()){
                    Order order = itemSnap.getValue(Order.class);
                    Calendar timeToCheck = Calendar.getInstance();
                    timeToCheck.setTimeInMillis(order.getCreateTimestamp());

                    if(now.get(Calendar.DAY_OF_YEAR) != timeToCheck.get(Calendar.DAY_OF_YEAR)){
                        ordersMap.put(itemSnap.getKey(), order);
                    }

                }

                loadOrders();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadOrders(){
        loader.setVisibility(View.INVISIBLE);
        ordersList.setVisibility(View.VISIBLE);
        if (ordersMap != null){
            orders = new ArrayList<>(ordersMap.values());
            Collections.sort(orders);
            orderListAdapter = new PastOrdersAdapter(orders, customersMap, this, getActivity());
            ordersList.setAdapter(orderListAdapter);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onItemClick(String id) {
        Order order = ordersMap.get(id);
        Intent intent = new Intent(getActivity(), FinishOrderActivity.class);
        intent.putExtra(SellFragment.FINISH_ORDER_KEY, order);
        intent.putExtra(ORDER_DETAILS_KEY, ORDER_DETAILS_KEY);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(String id) {

    }
}
