package com.core2plus.oalam.foodstudio.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.core2plus.oalam.foodstudio.API.APICall.RetrofitClient;
import com.core2plus.oalam.foodstudio.API.InsertResponse;
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

public class HistoryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        //historySwipeRefreshLayout=findViewById(R.id.historySwipeRefreshLayout);
        historySwipeRefreshLayout.setOnRefreshListener(this);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        historyRecyclerView.setLayoutManager(layoutManager);
        //getData();
        getData2();
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

    private void getData() {
//        historyList = h.getAll(getApplicationContext());
//        adapter = new HistoryAdapter(historyList);
//        historyRecyclerView.setAdapter(adapter);
    }

    private void getData2() {
        Log.v("history","getData2");
       // List<History> historyList = new ArrayList<>();
        final List<Purchase> purchaseList = new ArrayList<>();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.v("history","FirebaseAuth");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Call<PurchaseResponse> call= RetrofitClient.getInstance().getApi().getPurchasebyUserId(userId);
            call.enqueue(new Callback<PurchaseResponse>() {
                @Override
                public void onResponse(Call<PurchaseResponse> call, Response<PurchaseResponse> response) {
                    Log.v("history","onrespose");
                    PurchaseResponse purchaseResponse=response.body();
                    if (purchaseResponse.getSuccess() != 0) {
                        for (Purchase purchase : purchaseResponse.getPurchases()) {
                            purchaseList.add(purchase);
                            adapter = new HistoryAdapter(purchaseList);
                            historyRecyclerView.setAdapter(adapter);
                            Log.v("history",purchase.getImgUrl());
                            Log.v("history",purchase.getPurchaseTime());
                            Log.v("history",purchase.getUserid());
                            //Log.v("Divi", name.getProvID().toString());
//                            HashMap_MainArea.put(name.getLocationName(), Integer.parseInt(name.getLocationID()));
//                            MainArea.add(name.getLocationName());
//                            adapter5.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onFailure(Call<PurchaseResponse> call, Throwable t) {

                }
            });
//            historyList = h.getAll(getApplicationContext());
//            adapter = new HistoryAdapter(historyList);
//            historyRecyclerView.setAdapter(adapter);
    }

    }
    public void historyOnClickEvents(View v) {

        switch (v.getId()) {
            case R.id.backButton:
                finish();
                break;
            case R.id.clearButton:
                h.clearAll(getApplicationContext());
                Toast.makeText(getApplicationContext(), "The history is cleared, please refresh this page!", Toast.LENGTH_LONG).show();
                break;
        }
    }


}
