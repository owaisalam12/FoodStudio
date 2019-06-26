package com.core2plus.oalam.foodstudio.Fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.core2plus.oalam.foodstudio.API.APICall.RetrofitClient;
import com.core2plus.oalam.foodstudio.API.Purchase;
import com.core2plus.oalam.foodstudio.API.PurchaseResponse;
import com.core2plus.oalam.foodstudio.Adapter.HistoryAdapter;
import com.core2plus.oalam.foodstudio.Entity.History;
import com.core2plus.oalam.foodstudio.R;
import com.core2plus.oalam.foodstudio.SQLite.ORM.HistoryORM;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class PurchasesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    public PurchasesFragment() {
        // Required empty public constructor
    }

    // Init ui elements
    @BindView(R.id.historySwipeRefreshLayout)
    SwipeRefreshLayout historySwipeRefreshLayout;
    @BindView(R.id.historyRecyclerView)
    RecyclerView historyRecyclerView;
    // Variables
    HistoryORM h = new HistoryORM();
    List<History> historyList;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Purchases History");
        View view = inflater.inflate(R.layout.fragment_purchases, container, false);
        ButterKnife.bind(getActivity());
        setHasOptionsMenu(true);
        historySwipeRefreshLayout = view.findViewById(R.id.historySwipeRefreshLayout);
        historySwipeRefreshLayout.setOnRefreshListener(this);
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        historyRecyclerView.setLayoutManager(layoutManager);
        getData();


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menudelete, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.deleteAction) {
            //h.clearAll(getContext());
            //Toast.makeText(getContext(), "The history is cleared, please refresh this page!", Toast.LENGTH_LONG).show();
            showDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Отменяем анимацию обновления
                getData();

                historySwipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

    //    private void getData() {
//        historyList = h.getAll(getContext());
//        adapter = new HistoryAdapter(historyList);
//        historyRecyclerView.setAdapter(adapter);
//    }
    private void getData() {
        Log.v("history", "getData2");
        // List<History> historyList = new ArrayList<>();
        final List<Purchase> purchaseList = new ArrayList<>();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.v("history", "FirebaseAuth");
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.v("history", userId);

            Call<PurchaseResponse> call = RetrofitClient.getInstance().getApi().getPurchasebyUserId(userId);
            call.enqueue(new Callback<PurchaseResponse>() {
                @Override
                public void onResponse(Call<PurchaseResponse> call, Response<PurchaseResponse> response) {

                    PurchaseResponse purchaseResponse = response.body();
                    Log.v("history", purchaseResponse.toString());
                    if (purchaseResponse.getSuccess() != 0) {
                        for (Purchase purchase : purchaseResponse.getPurchases()) {
                            purchaseList.add(purchase);
                            adapter = new HistoryAdapter(purchaseList);
                            historyRecyclerView.setAdapter(adapter);
                            Log.v("history", purchase.getURl());
                            Log.v("history", purchase.getPurchaseTime());
                            Log.v("history", purchase.getUserid());
                            //Log.v("Divi", name.getProvID().toString());
//                            HashMap_MainArea.put(name.getLocationName(), Integer.parseInt(name.getLocationID()));
//                            MainArea.add(name.getLocationName());
//                            adapter5.notifyDataSetChanged();
                        }
                    } else {
                        Log.v("historypurch", purchaseResponse.getSuccess().toString());
                        purchaseList.clear();
                        adapter = new HistoryAdapter(purchaseList);
                        historyRecyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<PurchaseResponse> call, Throwable t) {
                    Log.v("history", t.getMessage());
                }
            });
//            historyList = h.getAll(getApplicationContext());
//            adapter = new HistoryAdapter(historyList);
//            historyRecyclerView.setAdapter(adapter);
        }

    }
//    public void historyOnClickEvents(View v) {
//
//        switch (v.getId()) {
//            case R.id.backButton:
//                //finish();
//                break;
//            case R.id.clearButton:
//                h.clearAll(getContext());
//                Toast.makeText(getContext(), "The history is cleared, please refresh this page!", Toast.LENGTH_LONG).show();
//                break;
//        }
//    }

    private void showDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirm dialog");
        builder.setMessage("You are about to delete all records. Do you really want to proceed?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                h.clearAll(getContext());
                getData();
                Toast.makeText(getContext(), "Records deleted!", Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getContext(), "You've changed your mind to delete all records", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

}
