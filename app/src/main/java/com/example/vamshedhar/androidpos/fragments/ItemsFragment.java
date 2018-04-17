package com.example.vamshedhar.androidpos.fragments;

import android.content.Context;
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
import android.widget.Toast;

import com.example.vamshedhar.androidpos.R;
import com.example.vamshedhar.androidpos.adapters.ItemListAdapter;
import com.example.vamshedhar.androidpos.objects.Item;

import java.util.ArrayList;
import java.util.HashMap;


public class ItemsFragment extends Fragment {

    HashMap<String, Item> itemsMap;

    private RecyclerView itemsList;
    private RecyclerView.Adapter itemListAdapter;
    private RecyclerView.LayoutManager itemsListLayoutManager;

    private ItemsFragmentListener mListener;

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
            itemListAdapter = new ItemListAdapter(items, (ItemListAdapter.ItemListInterface) getActivity());
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

    public interface ItemsFragmentListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }
}
