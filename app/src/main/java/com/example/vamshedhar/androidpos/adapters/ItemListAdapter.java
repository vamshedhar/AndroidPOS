package com.example.vamshedhar.androidpos.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vamshedhar.androidpos.R;
import com.example.vamshedhar.androidpos.objects.Item;

import java.util.ArrayList;

/**
 * Created by vamshedhar on 4/17/18.
 */

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private ArrayList<Item> items;
    private ItemListInterface IData;

    public interface ItemListInterface {
        void onItemClick(String id);
        void onItemLongClick(String id);
    }


    public ItemListAdapter(ArrayList<Item> items, ItemListInterface IData) {
        this.items = items;
        this.IData = IData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_list_item, parent, false);
        ItemListAdapter.ViewHolder viewHolder = new ItemListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = items.get(position);

        holder.itemName.setText(item.getName());
        holder.itemPrice.setText("$" + item.getPrice());

        holder.itemView.setTag(item.getId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemId = (String) view.getTag();
                IData.onItemClick(itemId);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String itemId = (String) view.getTag();
                IData.onItemLongClick(itemId);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            itemName  = itemView.findViewById(R.id.itemName);
            itemPrice  = itemView.findViewById(R.id.itemPrice);
        }
    }
}