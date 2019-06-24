package com.core2plus.oalam.foodstudio.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.core2plus.oalam.foodstudio.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class DonateFragment extends Fragment {


    public DonateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Donate Us");
        return inflater.inflate(R.layout.fragment_donate, container, false);
    }

}
