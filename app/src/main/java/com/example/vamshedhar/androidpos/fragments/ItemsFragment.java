package com.example.vamshedhar.androidpos.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.vamshedhar.androidpos.R;
import com.example.vamshedhar.androidpos.adapters.ItemListAdapter;
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
import static android.widget.Toast.makeText;


public class ItemsFragment extends Fragment implements ItemListAdapter.ItemListInterface {

    HashMap<String, Item> itemsMap;

    private RecyclerView itemsList;
    private RecyclerView.Adapter itemListAdapter;
    private RecyclerView.LayoutManager itemsListLayoutManager;
    private ImageView addItemBtn;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private DatabaseReference itemsReference;
    private String username;

    public ItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_items, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        itemsList = getView().findViewById(R.id.itemsList);
        addItemBtn = getView().findViewById(R.id.addItemBtn);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        itemsReference = databaseReference.child("items");

        username = currentUser.getEmail().substring(0, currentUser.getEmail().indexOf('@'));

        itemsListLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        itemsList.setLayoutManager(itemsListLayoutManager);

        fetchItems();

        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemClick();
            }
        });
    }

    public void addItemClick(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View textEntryView = inflater.inflate(R.layout.edit_item, null);

        final EditText itemNameET = textEntryView.findViewById(R.id.itemName);
        final EditText itemPriceET = textEntryView.findViewById(R.id.itemPrice);


        final AlertDialog.Builder addItemAlert = new AlertDialog.Builder(getActivity());

        addItemAlert.setTitle("Add Item:").setView(textEntryView).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addItem(itemNameET.getText().toString().trim(), itemPriceET.getText().toString().trim());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Nothing happens here
            }
        });

        addItemAlert.show();
    }

    public boolean isValidItem(String itemName, String itemPrice){
        if (itemName.isEmpty()){
            return false;
        }

        try {
            Double.parseDouble(itemPrice);
        } catch (Exception e){
            return false;
        }

        return true;
    }

    public void addItem(String itemName, String itemPrice){

        if (!isValidItem(itemName, itemPrice)){
            Toast.makeText(getActivity(), "Please Enter Valid Details!", LENGTH_SHORT).show();
            return;
        }

        String id = itemsReference.push().getKey();
        Item item = new Item(id, itemName, username, Double.parseDouble(itemPrice));

        itemsReference.child(id).setValue(item);

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
            itemListAdapter = new ItemListAdapter(items, getActivity(), this);
            itemsList.setAdapter(itemListAdapter);
        }
    }


    private void updateItem(String id, String newName, String newPrice){

        if (!isValidItem(newName, newPrice)){
            Toast.makeText(getActivity(), "Please Enter Valid Details!", LENGTH_SHORT).show();
            return;
        }

        Item item = itemsMap.get(id);
        item.setName(newName);
        item.setPrice(Double.parseDouble(newPrice));

        itemsReference.child(id).setValue(item);
    }

    private void deleteItem(String id){
        itemsReference.child(id).removeValue();
    }

    @Override
    public void onItemUpdate(String id) {

        final Item item = itemsMap.get(id);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View textEntryView = inflater.inflate(R.layout.edit_item, null);

        final EditText itemNameET = textEntryView.findViewById(R.id.itemName);
        final EditText itemPriceET = textEntryView.findViewById(R.id.itemPrice);

        itemNameET.setText(item.getName());
        itemPriceET.setText(item.getPrice() + "");

        itemNameET.setTag(item.getId());

        final AlertDialog.Builder editItemAlert = new AlertDialog.Builder(getActivity());

        editItemAlert.setTitle("Update Item:").setView(textEntryView).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateItem(item.getId(), itemNameET.getText().toString().trim(), itemPriceET.getText().toString().trim());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Nothing happens here
            }
        });

        editItemAlert.show();
    }

    @Override
    public void onItemLongClick(String id) {
        AlertDialog.Builder deleteItemDiag = new AlertDialog.Builder(getActivity());

        final Item item = itemsMap.get(id);

        deleteItemDiag.setTitle("Are you sure you want to delete " + item.getName() + "?")
            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deleteItem(item.getId());
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        deleteItemDiag.show();
    }
}
