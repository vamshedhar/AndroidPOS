package com.example.vamshedhar.androidpos.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vamshedhar.androidpos.R;
import com.example.vamshedhar.androidpos.objects.Item;
import com.example.vamshedhar.androidpos.objects.OrderItem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vamshedhar on 4/24/18.
 */

public class OrderDetailsListAdapter extends RecyclerView.Adapter<OrderDetailsListAdapter.ViewHolder> {

    private HashMap<String, Item> itemHashMap;
    private ArrayList<OrderItem> orderItemList;

    public OrderDetailsListAdapter(ArrayList<OrderItem> orderItemList, HashMap<String, Item> itemHashMap) {
        this.orderItemList = orderItemList;
        this.itemHashMap = itemHashMap;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderdetails_item, parent, false);
        OrderDetailsListAdapter.ViewHolder viewHolder = new OrderDetailsListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OrderItem orderItem = orderItemList.get(position);

        Item item = itemHashMap.get(orderItem.getItemId());

        holder.itemDetails.setText(item.getName() + " (" + orderItem.getQuantity() + ")");
        holder.saleAmt.setText(orderItem.getAmount()+"");

    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemDetails, saleAmt;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            itemDetails  = itemView.findViewById(R.id.customerName);
            saleAmt  = itemView.findViewById(R.id.amount);
        }
    }
}
