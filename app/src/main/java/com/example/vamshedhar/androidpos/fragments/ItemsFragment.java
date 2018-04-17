package com.example.vamshedhar.androidpos.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.Toast;

import com.example.vamshedhar.androidpos.R;
import com.example.vamshedhar.androidpos.adapters.ItemListAdapter;
import com.example.vamshedhar.androidpos.objects.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;


public class ItemsFragment extends Fragment implements ItemListAdapter.ItemListInterface {

    HashMap<String, Item> itemsMap;

    private RecyclerView itemsList;
    private RecyclerView.Adapter itemListAdapter;
    private RecyclerView.LayoutManager itemsListLayoutManager;

    private ItemsFragmentListener mListener;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
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

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        username = currentUser.getEmail().substring(0, currentUser.getEmail().indexOf('@'));

        itemsMap = new HashMap<>();

        for (int i = 0; i < 20; i++) {
            itemsMap.put(i + "", new Item(i + "", "Demo Item " + i, "vamshedhar", 20 + i));
        }

        itemsListLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        itemsList.setLayoutManager(itemsListLayoutManager);
        loadItems();
    }

    public void loadItems(){
        if (itemsMap != null){
            ArrayList<Item> items = new ArrayList<>(itemsMap.values());
            itemListAdapter = new ItemListAdapter(items, getActivity(), this);
            itemsList.setAdapter(itemListAdapter);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof ItemsFragmentListener) {
//            mListener = (ItemsFragmentListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement ItemsFragmentListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    private void updateItem(String id, String newName, String newPrice){
        Item item = itemsMap.get(id);
        item.setName(newName);
        item.setPrice(Float.parseFloat(newPrice));
        itemsMap.put(item.getId(), item);
        loadItems();
    }

    private void deleteItem(String id){
        itemsMap.remove(id);
        loadItems();
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

    public interface ItemsFragmentListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }
}
