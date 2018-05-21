package com.example.vamshedhar.androidpos.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vamshedhar.androidpos.R;
import com.example.vamshedhar.androidpos.adapters.OrderDetailsListAdapter;
import com.example.vamshedhar.androidpos.fragments.PastOrdersFragment;
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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;

public class FinishOrderActivity extends AppCompatActivity {

    private Order order;
    private Customer selectedCustomer;
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
    private DatabaseReference customersReference;
    private String username;

    private OkHttpClient mClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_order);

        customerName = findViewById(R.id.customerName);
        customerNumber = findViewById(R.id.customerPhone);
        subTotal = findViewById(R.id.subTotal);
        taxAmount = findViewById(R.id.taxAmount);
        grandTotal = findViewById(R.id.grandTotal);
        itemsList = findViewById(R.id.orderItems);
        completeOrder = findViewById(R.id.confirmOrder);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(SellFragment.FINISH_ORDER_KEY)){
            order = (Order) getIntent().getExtras().getSerializable(SellFragment.FINISH_ORDER_KEY);
            if (getIntent().getExtras().containsKey(PastOrdersFragment.ORDER_DETAILS_KEY)){
                completeOrder.setVisibility(View.INVISIBLE);
            }
        } else {
            finish();
        }


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        username = currentUser.getEmail().substring(0, currentUser.getEmail().indexOf('@'));

        selectedCustomer = null;

        databaseReference = FirebaseDatabase.getInstance().getReference().child(username);
        itemsReference = databaseReference.child("items");
        ordersReference = databaseReference.child("orders");
        customersReference = databaseReference.child("customers");

        if (order.getCustomerId() != null){
            customersReference.child(order.getCustomerId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    selectedCustomer = dataSnapshot.getValue(Customer.class);
                    loadCustomerDetails();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            customerName.setText("No Customer Added");
            customerNumber.setText("");
        }

        itemsListLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        itemsList.setLayoutManager(itemsListLayoutManager);

        subTotal.setText(Math.floor(order.getBaseValue()*100)/100 + "");
        taxAmount.setText(Math.floor(order.getTax()*100)/100 + "");
        grandTotal.setText(Math.floor(order.getTotalAmount()*100)/100 + "");

        fetchItems();

        completeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = ordersReference.push().getKey();
                order.setId(id);
                ordersReference.child(id).setValue(order);

                if (selectedCustomer != null){
                    DatabaseReference selectedCustomerRef = customersReference.child(selectedCustomer.getId());

                    selectedCustomerRef.child("totalOrders").setValue(selectedCustomer.getTotalOrders() + 1);
                    selectedCustomerRef.child("totalOrderAmount").setValue(selectedCustomer.getTotalOrderAmount() + order.getTotalAmount());
                    selectedCustomerRef.child("lastOrder").setValue(order.getCreatedTime());

                    try {
                        post("https://restropos.herokuapp.com/sms", "Thank you for ordering with us! You order total is $" + order.getTotalAmount(), selectedCustomer.getPhone_no(),  new  Callback(){

                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                    }
                                });
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    Call post(String url, String text, String number, Callback callback) throws IOException{
        Log.d(MainActivity.TAG, number + " " + text);
        RequestBody formBody = new FormBody.Builder()
                .add("To", "+1" + number)
                .add("Body", text)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        Call response = mClient.newCall(request);
        response.enqueue(callback);
        return response;

    }

    public void loadCustomerDetails(){
        if (selectedCustomer != null){
            customerName.setText(selectedCustomer.getName());
            customerNumber.setText(selectedCustomer.getPhone_no());
        } else {
            customerName.setText("No Customer Added");
            customerNumber.setText("");
        }
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
