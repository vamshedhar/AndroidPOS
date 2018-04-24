package com.example.vamshedhar.androidpos.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vamshedhar.androidpos.R;
import com.example.vamshedhar.androidpos.adapters.OrderDetailsListAdapter;
import com.example.vamshedhar.androidpos.adapters.SellItemListAdapter;
import com.example.vamshedhar.androidpos.fragments.SellFragment;
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
import java.util.HashMap;

public class FinishOrderActivity extends AppCompatActivity {

    private Order order;
    private TextView customerName, customerNumber, subTotal, taxAmount, grandTotal;
    private Button completeOrder;
    HashMap<String, Item> itemsMap;

    private RecyclerView itemsList;
    private RecyclerView.Adapter itemListAdapter;
    private RecyclerView.LayoutManager itemsListLayoutManager;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private DatabaseReference itemsReference;
    private DatabaseReference ordersReference;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_order);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(SellFragment.FINISH_ORDER_KEY)){
            order = (Order) getIntent().getExtras().getSerializable(SellFragment.FINISH_ORDER_KEY);
        } else {
            finish();
        }

        customerName = findViewById(R.id.customerName);
        customerNumber = findViewById(R.id.customerPhone);
        subTotal = findViewById(R.id.subTotal);
        taxAmount = findViewById(R.id.taxAmount);
        grandTotal = findViewById(R.id.grandTotal);
        itemsList = findViewById(R.id.orderItems);
        completeOrder = findViewById(R.id.confirmOrder);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        itemsReference = databaseReference.child("items");
        ordersReference = databaseReference.child("orders");

        username = currentUser.getEmail().substring(0, currentUser.getEmail().indexOf('@'));

        itemsListLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        itemsList.setLayoutManager(itemsListLayoutManager);

        subTotal.setText(order.getBaseValue()+"");
        taxAmount.setText(order.getTax()+"");
        grandTotal.setText(order.getTotalAmount()+"");

        fetchItems();

        completeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = ordersReference.push().getKey();
                order.setId(id);
                ordersReference.child(id).setValue(order);

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

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
            itemListAdapter = new OrderDetailsListAdapter(order.getItems(), itemsMap);
            itemsList.setAdapter(itemListAdapter);
        }
    }
}
