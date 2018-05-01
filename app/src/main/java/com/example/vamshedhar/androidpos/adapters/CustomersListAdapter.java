package com.example.vamshedhar.androidpos.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vamshedhar.androidpos.R;
import com.example.vamshedhar.androidpos.activities.MainActivity;
import com.example.vamshedhar.androidpos.objects.Customer;
import com.example.vamshedhar.androidpos.objects.Item;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;

public class CustomersListAdapter extends RecyclerView.Adapter<CustomersListAdapter.ViewHolder> {

    private ArrayList<Customer> customers;
    private CustomersListInterface IData;
    private Context context;


    public interface CustomersListInterface {
        void onCustomerUpdate(String id);
        void onItemLongClick(String id);
    }


    public CustomersListAdapter(ArrayList<Customer> customers, Context context, CustomersListInterface IData) {
        this.customers = customers;
        this.context = context;
        this.IData = IData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomersListAdapter.ViewHolder holder, int position) {
        final Customer customer = customers.get(position);


        holder.customerName.setText(customer.getName());
        holder.customerPhone.setText(customer.getPhone_no());

        holder.customerView.setTag(customer.getId());

        Log.d(MainActivity.TAG, customer.getLastOrderTime() + "");

        if (customer.getLastOrderTime() != -1){
            PrettyTime prettyTime = new PrettyTime();
            holder.lastOrder.setText("Last Order: " + prettyTime.format(new Date(customer.getLastOrderTime())));
        } else {
            holder.lastOrder.setText("");
        }


        holder.customerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IData.onCustomerUpdate((String) view.getTag());
            }
        });

        holder.customerView.setOnLongClickListener(new View.OnLongClickListener() {
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
        return customers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView customerName, customerPhone, lastOrder;
        View customerView;
        public ViewHolder(View customerView) {
            super(customerView);

            this.customerView = customerView;
            customerName  = customerView.findViewById(R.id.customerName);
            customerPhone  = customerView.findViewById(R.id.customerPhone);
            lastOrder = customerView.findViewById(R.id.lastOrder);
        }
    }
}
