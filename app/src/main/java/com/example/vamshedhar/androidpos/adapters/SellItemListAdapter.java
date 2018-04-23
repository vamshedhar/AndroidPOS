package com.example.vamshedhar.androidpos.adapters;

import android.content.Context;
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
 * Created by vamshedhar on 4/22/18.
 */

public class SellItemListAdapter extends RecyclerView.Adapter<SellItemListAdapter.ViewHolder> {

    private ArrayList<Item> items;
    private HashMap<String, OrderItem> orderItemHashMap;
    private SellItemListInterface IData;
    private Context context;

    public interface SellItemListInterface {
        void onItemAdd(String id, int quantity);
        void onItemLongClick(String id);
    }

    public SellItemListAdapter(ArrayList<Item> items, HashMap<String, OrderItem> orderItemHashMap, Context context, SellItemListInterface IData) {
        this.items = items;
        this.context = context;
        this.IData = IData;
        this.orderItemHashMap = orderItemHashMap;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sell_list_item, parent, false);
        SellItemListAdapter.ViewHolder viewHolder = new SellItemListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final SellItemListAdapter.ViewHolder holder, int position) {
        Item item = items.get(position);

        holder.itemDetails.setText(item.getName() + " ($" + item.getPrice() + ")");
        if (orderItemHashMap.containsKey(item.getId())){
            holder.saleQty.setText(orderItemHashMap.get(item.getId()).getQuantity() + "");
        } else {
            holder.saleQty.setText("");
        }

        holder.itemView.setTag(item.getId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.saleQty.getText().toString().isEmpty()){
                    IData.onItemAdd((String) view.getTag(), 1);
                    holder.saleQty.setText("1");
                } else {
                    int quantity = Integer.parseInt(holder.saleQty.getText().toString()) + 1;
                    IData.onItemAdd((String) view.getTag(), quantity);
                    holder.saleQty.setText(quantity + "");
                }


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

    public void setOrderItemHashMap(HashMap<String, OrderItem> orderItemHashMap){
        this.orderItemHashMap = orderItemHashMap;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemDetails, saleQty;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            itemDetails  = itemView.findViewById(R.id.itemDetails);
            saleQty  = itemView.findViewById(R.id.saleQty);
        }
    }
}
