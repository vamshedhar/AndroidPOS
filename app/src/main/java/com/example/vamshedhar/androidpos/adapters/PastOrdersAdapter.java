package com.example.vamshedhar.androidpos.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vamshedhar.androidpos.R;
import com.example.vamshedhar.androidpos.activities.MainActivity;
import com.example.vamshedhar.androidpos.objects.Customer;
import com.example.vamshedhar.androidpos.objects.Item;
import com.example.vamshedhar.androidpos.objects.Order;
import com.example.vamshedhar.androidpos.objects.OrderItem;

//import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by vamshedhar on 5/1/18.
 */

public class PastOrdersAdapter extends RecyclerView.Adapter<PastOrdersAdapter.ViewHolder> {

    private ArrayList<Order> orders;
    private HashMap<String, Customer> customersMap;
    private PastOrdersAdapter.PastOrdersListInterface IData;
    private Context context;

    public PastOrdersAdapter(ArrayList<Order> orders, HashMap<String, Customer> customersMap, PastOrdersListInterface IData, Context context) {
        this.orders = orders;
        this.IData = IData;
        this.context = context;
        this.customersMap = customersMap;
    }

    public interface PastOrdersListInterface {
        void onItemClick(String id);
        void onItemLongClick(String id);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_history_item, parent, false);
        PastOrdersAdapter.ViewHolder viewHolder = new PastOrdersAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Order order = orders.get(position);

        holder.orderID.setText(order.getId().substring(1, 8));
        holder.orderAmount.setText("$" + (int) Math.ceil(order.getTotalAmount()) + "");

//        PrettyTime prettyTime = new PrettyTime();
//
//        holder.orderTime.setText(prettyTime.format(new Date(order.getCreateTimestamp())));

        if (order.getCustomerId() != null && !order.getCustomerId().isEmpty() && customersMap.containsKey(order.getCustomerId())){
            Customer customer = customersMap.get(order.getCustomerId());
            holder.customerDetails.setText(customer.getName() + " (" + customer.getPhone_no() +")");
        } else {
            holder.customerDetails.setText("No Customer Details");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderID, orderAmount, customerDetails, orderTime;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            orderID  = itemView.findViewById(R.id.orderID);
            orderAmount  = itemView.findViewById(R.id.orderAmount);
            customerDetails  = itemView.findViewById(R.id.customerDetails);
            orderTime  = itemView.findViewById(R.id.timeAgo);
        }
    }
}
