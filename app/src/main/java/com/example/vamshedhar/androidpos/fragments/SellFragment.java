package com.example.vamshedhar.androidpos.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vamshedhar.androidpos.R;
import com.example.vamshedhar.androidpos.activities.AddCustomerActivity;
import com.example.vamshedhar.androidpos.activities.FinishOrderActivity;
import com.example.vamshedhar.androidpos.adapters.ItemListAdapter;
import com.example.vamshedhar.androidpos.adapters.SellItemListAdapter;
import com.example.vamshedhar.androidpos.objects.Customer;
import com.example.vamshedhar.androidpos.objects.Item;
import com.example.vamshedhar.androidpos.objects.Order;
import com.example.vamshedhar.androidpos.objects.OrderItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class SellFragment extends Fragment implements SellItemListAdapter.SellItemListInterface {

    HashMap<String, Item> itemsMap;
    HashMap<String, OrderItem> orderItemsMap;
    Customer selectedCustomer;

    public static final int ADD_CUSTOMER = 44;
    public static final int FINISH_ORDER = 45;

    public static final String FINISH_ORDER_KEY = "ORDER_DETAILS";
    public static final String CUSTOMER_DETAILS = "CUSTOMER_DETAILS";

    private RecyclerView itemsList;
    private SellItemListAdapter itemListAdapter;
    private RecyclerView.LayoutManager itemsListLayoutManager;
    private ProgressBar loader;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private DatabaseReference itemsReference;
    private String username;

    private RelativeLayout customerCard;
    private Button completeOrder;
    private TextView addCustomerLabel;


    public SellFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sell, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        itemsList = getView().findViewById(R.id.itemsList);
        customerCard = getView().findViewById(R.id.customerCard);
        completeOrder = getView().findViewById(R.id.completeOrderButton);
        addCustomerLabel = getView().findViewById(R.id.addCustomerLabel);
        loader = getView().findViewById(R.id.loader);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        selectedCustomer = null;

        databaseReference = FirebaseDatabase.getInstance().getReference();
        itemsReference = databaseReference.child("items");

        username = currentUser.getEmail().substring(0, currentUser.getEmail().indexOf('@'));

        itemsListLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        itemsList.setLayoutManager(itemsListLayoutManager);

        orderItemsMap = new HashMap<>();

        fetchItems();

        customerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddCustomerActivity.class);

                startActivityForResult(intent, ADD_CUSTOMER);
            }
        });

        completeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Order order = new Order();
                order.setId("");
                order.setCreatedUser(username);

                if (selectedCustomer != null){
                    order.setCustomerId(selectedCustomer.getId());
                }

                for (OrderItem orderItem : orderItemsMap.values()){
                    order.addItem(orderItem);
                }

                if (orderItemsMap.size() > 0) {



                    Intent intent = new Intent(getActivity(), FinishOrderActivity.class);
                    intent.putExtra(FINISH_ORDER_KEY, order);

                    startActivityForResult(intent, FINISH_ORDER);
                }
                else{
                    Toast.makeText(getActivity(), "No Items Selected", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FINISH_ORDER && resultCode == getActivity().RESULT_OK){
            orderItemsMap = new HashMap<>();
            fetchItems();
            completeOrder.setText(getString(R.string.complete_order));
            addCustomerLabel.setText("Add Customer");
            selectedCustomer = null;
        } else if (requestCode == ADD_CUSTOMER && resultCode == getActivity().RESULT_OK){
            selectedCustomer = (Customer) data.getSerializableExtra(CUSTOMER_DETAILS);
            loadCustomerDetails();
        }
    }
    
    public void loadCustomerDetails(){
        addCustomerLabel.setText(selectedCustomer.getName() + " (" + selectedCustomer.getPhone_no() +")");
    }

    public void fetchItems(){
        loader.setVisibility(View.VISIBLE);
        itemsList.setVisibility(View.INVISIBLE);
        itemsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemsMap = new HashMap<>();

                for (DataSnapshot itemSnap : dataSnapshot.getChildren()){
                    itemsMap.put(itemSnap.getKey(), itemSnap.getValue(Item.class));
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
        itemsList.setVisibility(View.VISIBLE);
        if (itemsMap != null){
            ArrayList<Item> items = new ArrayList<>(itemsMap.values());
            Collections.sort(items);
            itemListAdapter = new SellItemListAdapter(items, orderItemsMap, getActivity(), this);
            itemsList.setAdapter(itemListAdapter);
        }
    }

    public void recalculateTotals(){
        Order order = new Order();
        for (OrderItem orderItem : orderItemsMap.values()){
            order.addItem(orderItem);
        }

        completeOrder.setText(getString(R.string.complete_order) + " ($" + order.getTotalAmount() + ")");

    }

    @Override
    public void onItemAdd(String id, int quantity) {
        Item item = itemsMap.get(id);
        OrderItem orderItem = new OrderItem(id, quantity, item.getPrice() * quantity);
        orderItemsMap.put(id, orderItem);
        itemListAdapter.setOrderItemHashMap(orderItemsMap);

        recalculateTotals();
    }

    @Override
    public void onItemLongClick(String id) {
        orderItemsMap.remove(id);
        itemListAdapter.setOrderItemHashMap(orderItemsMap);
        recalculateTotals();
    }
}
