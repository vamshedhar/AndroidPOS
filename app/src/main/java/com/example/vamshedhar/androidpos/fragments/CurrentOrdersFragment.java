package com.example.vamshedhar.androidpos.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vamshedhar.androidpos.R;

public class CurrentOrdersFragment extends Fragment {

    public CurrentOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current_orders, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


}
