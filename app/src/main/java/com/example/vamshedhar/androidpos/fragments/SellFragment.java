package com.example.vamshedhar.androidpos.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.vamshedhar.androidpos.R;
import com.example.vamshedhar.androidpos.adapters.ItemListAdapter;
import com.example.vamshedhar.androidpos.adapters.SellItemListAdapter;
import com.example.vamshedhar.androidpos.objects.Item;
import com.example.vamshedhar.androidpos.objects.OrderItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class SellFragment extends Fragment implements SellItemListAdapter.SellItemListInterface {

    HashMap<String, Item> itemsMap;
    HashMap<String, OrderItem> orderItemsMap;

    private RecyclerView itemsList;
    private SellItemListAdapter itemListAdapter;
    private RecyclerView.LayoutManager itemsListLayoutManager;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private DatabaseReference itemsReference;
    private String username;

    private RelativeLayout customerCard;
    private Button completeOrder;


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

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        itemsReference = databaseReference.child("items");

        username = currentUser.getEmail().substring(0, currentUser.getEmail().indexOf('@'));

        itemsListLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        itemsList.setLayoutManager(itemsListLayoutManager);

        orderItemsMap = new HashMap<>();

        fetchItems();
    }

    public void fetchItems(){
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
        if (itemsMap != null){
            ArrayList<Item> items = new ArrayList<>(itemsMap.values());
            itemListAdapter = new SellItemListAdapter(items, orderItemsMap, getActivity(), this);
            itemsList.setAdapter(itemListAdapter);
        }
    }

    @Override
    public void onItemAdd(String id, int quantity) {
        Item item = itemsMap.get(id);
        OrderItem orderItem = new OrderItem(id, quantity, item.getPrice() * quantity);
        orderItemsMap.put(id, orderItem);
        itemListAdapter.setOrderItemHashMap(orderItemsMap);
    }

    @Override
    public void onItemLongClick(String id) {

    }
}
