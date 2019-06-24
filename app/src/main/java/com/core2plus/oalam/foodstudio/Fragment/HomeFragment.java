package com.core2plus.oalam.foodstudio.Fragment;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.core2plus.oalam.foodstudio.API.APICall.RetrofitClient;
import com.core2plus.oalam.foodstudio.API.Adapter.Recycleradapter;
import com.core2plus.oalam.foodstudio.API.Adapter.RecycleradapterUpcoming;
import com.core2plus.oalam.foodstudio.API.Data;
import com.core2plus.oalam.foodstudio.API.Deal;
import com.core2plus.oalam.foodstudio.API.DealResponse;
import com.core2plus.oalam.foodstudio.API.Slider;
import com.core2plus.oalam.foodstudio.API.SliderResponse;
import com.core2plus.oalam.foodstudio.Activity.DashboardActivity;
import com.core2plus.oalam.foodstudio.Activity.QRActivity;
import com.core2plus.oalam.foodstudio.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.kitek.timertextview.TimerTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    SliderLayout sliderLayout;
    RecyclerView recyclerView;
    RecyclerView recyclerViewUpcomingItems;
    SwipeRefreshLayout swipeRefreshLayout;
    SwipeRefreshLayout swipeRefreshLayoutUpcomingItems;
    List<DealResponse> listing;
    List<SliderResponse> sliderList;
    public int listsize;
    List<DealResponse> listingUpcomingItems;
    Button scanQR;
    Context mContext;
    Recycleradapter recyclerAdapter;
    RecycleradapterUpcoming recyclerAdapterUpcomingItems;
    SharedPreferences prefs;
    TimerTextView timerText;
    TextView textViewAvailable, textViewUpcoming;
    ShimmerFrameLayout mShimmerViewContainer;
    ShimmerFrameLayout mShimmerViewContainerUpcomingItems;
    //private long BLOCK_TIME = 86400000; // 24houurs in miliseconds
    private long BLOCK_TIME = 20000;
    private String Img_URL = "http://192.168.137.1/food/assets/images/banners/";
    private static String DEFAULT_CHANNEL_ID = "default_channel";

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");
        scanQR = view.findViewById(R.id.scanQRButton);
        mContext = getActivity();
        swipeRefreshLayout = view.findViewById(R.id.swipeContainer);
        swipeRefreshLayoutUpcomingItems = view.findViewById(R.id.swipeContainerUpcomingItems);
        sliderLayout = view.findViewById(R.id.imageSlider);
        recyclerAdapter = new Recycleradapter(listing, ImageLoader.getInstance());
        recyclerAdapterUpcomingItems = new RecycleradapterUpcoming(listingUpcomingItems, ImageLoader.getInstance());
        textViewAvailable = view.findViewById(R.id.TVnodealsAvailable);
        textViewUpcoming = view.findViewById(R.id.TVnodealsUpcoming);
        sliderLayout.setIndicatorAnimation(IndicatorAnimations.SWAP); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);
        sliderLayout.setScrollTimeInSec(2); //set scroll delay in seconds :
        listing = new ArrayList<>();
        sliderList = new ArrayList<>();
        listingUpcomingItems = new ArrayList<>();
        getsliders();
        //setSliderViews(listsize);
//        timerText = (TimerTextView) view.findViewById(R.id.timerText);
        recyclerView = view.findViewById(R.id.recycle_availableItems);
        recyclerViewUpcomingItems = view.findViewById(R.id.recycle_UpcomingItems);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext()).build();
        ImageLoader.getInstance().init(config);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        mShimmerViewContainerUpcomingItems = view.findViewById(R.id.shimmer_view_containerUpcomingItems);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mShimmerViewContainer.startShimmer();
                mShimmerViewContainer.setVisibility(View.VISIBLE);
                textViewUpcoming.setVisibility(View.GONE);
                textViewAvailable.setVisibility(View.GONE);
                listing.clear();
                mShimmerViewContainerUpcomingItems.startShimmer();
                mShimmerViewContainerUpcomingItems.setVisibility(View.VISIBLE);
                listingUpcomingItems.clear();
                // implement Handler to wait for 3 seconds and then update UI means update value of TextView
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // cancle the Visual indication of a refresh
                        swipeRefreshLayout.setRefreshing(false);
                        loadDeals();
                        loadUpcomingItems();
                        recyclerAdapter.notifyDataSetChanged();
                        recyclerAdapterUpcomingItems.notifyDataSetChanged();
                    }
                }, 3000);
            }
        });


        checkTimer();
        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                new CountDownTimer(60000, 1000) {
//
//                    public void onTick(long millisUntilFinished) {
//                        scanQR.setEnabled(false);
//                        scanQR.setBackground(getActivity().getDrawable(R.drawable.round_buttongrey));
//                        scanQR.setText("Wait for " + DateUtils.formatElapsedTime( millisUntilFinished / 1000));
//                    }
//
//                    public void onFinish() {
//                        scanQR.setEnabled(true);
//                        scanQR.setBackground(getActivity().getDrawable(R.drawable.round_buttonred));
//                        scanQR.setText("Scan QR");
//                        Log.v("long","1 true else");
//
//
//                    }
//                }.start();

//                for(int i=0;i<sliderList.size();i++){
//                    Log.v("sld",String.valueOf(sliderList.size()));
//                    Log.v("sld",sliderList.get(i).getSlider().get(i).getId());
//                    Log.v("sld",sliderList.get(i).getSlider().get(i).getImgUrl());
//                     Log.v("sld",sliderList.get(i).getSlider().get(i).getDescription());
//                }

                SharedPreferences prefs = getActivity().getSharedPreferences("time", Context.MODE_PRIVATE);
                long currentTime = new Date().getTime();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("time", currentTime);
                editor.apply();
                scanQR.setEnabled(false);
                //scanQR.setBackground(getActivity().getResources().getDrawable(R.drawable.round_buttongrey));
                startActivity(new Intent(getActivity(), QRActivity.class));


            }
        });


//        Call<ItemsResponse> call = RetrofitClient.getInstance().getApi().getItems();
//                call.enqueue(new Callback<ItemsResponse>() {
//                    @Override
//                    public void onResponse(Call<ItemsResponse> call, Response<ItemsResponse> response) {
//                        ItemsResponse itemsResponse = response.body();
//                        ItemsResponse itm = null;
//                        //container2.setVisibility(View.GONE);
//                        List<Data> dataList = new ArrayList<>();
//                        Data data2 = new Data();
//                        for (Data data : itemsResponse.getData()) {
//                            itm = new ItemsResponse();
//                            String name = data.getName();
//                            String image = data.getImage();
//
//                            Log.v("HOME", name);
//                            Log.v("HOME", image);
////                data2.setName(name);
////                data2.setImage(image);
////                dataList.add(data2);
//                            listing.add(itemsResponse);
//
//                        }

        loadDeals();
        loadUpcomingItems();
        return view;
    }

    private void loadDeals() {

        Call<DealResponse> call = RetrofitClient.getInstance().getApi().getDeals();
        call.enqueue(new Callback<DealResponse>() {
            @Override
            public void onResponse(Call<DealResponse> call, Response<DealResponse> response) {
                DealResponse dealResponse = response.body();
                DealResponse itm = null;
                //container2.setVisibility(View.GONE);
                List<Data> dataList = new ArrayList<>();
                Data data2 = new Data();
                if (dealResponse.getSuccess() != 0) {
                    for (Deal deal : dealResponse.getDeals()) {
                        itm = new DealResponse();
                        String name = deal.getDealsName();
                        String image = deal.getImg();

                        Log.v("HOME", name);
                        Log.v("HOME", image);
//                data2.setName(name);
//                data2.setImage(image);
//                dataList.add(data2);
                        listing.add(dealResponse);

                    }
                    recyclerAdapter = new Recycleradapter(listing, ImageLoader.getInstance());
                    RecyclerView.LayoutManager recyce = new LinearLayoutManager(getContext(), GridLayoutManager.HORIZONTAL, false);
                    // RecyclerView.LayoutManager recyce = new LinearLayoutManager(MainActivity.this);
                    //recyclerView.addItemDecoration(new GridSpacingdecoration(2, dpToPx(10), true));
                    recyclerView.setLayoutManager(recyce);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(recyclerAdapter);

                    mShimmerViewContainer.stopShimmer();
                    mShimmerViewContainer.setVisibility(View.GONE);


                } else {


                    mShimmerViewContainer.stopShimmer();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    // Toast.makeText(getActivity(), "No deals available at the moment", Toast.LENGTH_SHORT).show();
                    textViewAvailable.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<DealResponse> call, Throwable t) {

            }
        });
    }

    private void loadUpcomingItems() {

        Call<DealResponse> call = RetrofitClient.getInstance().getApi().getdealsUpcoming();
        call.enqueue(new Callback<DealResponse>() {
            @Override
            public void onResponse(Call<DealResponse> call, Response<DealResponse> response) {
                DealResponse dealResponse = response.body();
                DealResponse itm = null;
                //container2.setVisibility(View.GONE);
                List<Data> dataList = new ArrayList<>();
                Data data2 = new Data();
                if (dealResponse.getSuccess() != 0) {
                    for (Deal deal : dealResponse.getDeals()) {
                        itm = new DealResponse();
                        String name = deal.getDealsName();
                        String image = deal.getImg();

                        Log.v("HOME", name);
                        Log.v("HOME", image);
//                data2.setName(name);
//                data2.setImage(image);
//                dataList.add(data2);
                        listingUpcomingItems.add(dealResponse);

                    }
                    recyclerAdapterUpcomingItems = new RecycleradapterUpcoming(listingUpcomingItems, ImageLoader.getInstance());
                    RecyclerView.LayoutManager recyce = new LinearLayoutManager(getContext(), GridLayoutManager.HORIZONTAL, false);
                    // RecyclerView.LayoutManager recyce = new LinearLayoutManager(MainActivity.this);
                    //recyclerView.addItemDecoration(new GridSpacingdecoration(2, dpToPx(10), true));
                    recyclerViewUpcomingItems.setLayoutManager(recyce);
                    recyclerViewUpcomingItems.setItemAnimator(new DefaultItemAnimator());
                    recyclerViewUpcomingItems.setAdapter(recyclerAdapterUpcomingItems);

                    mShimmerViewContainerUpcomingItems.stopShimmer();
                    mShimmerViewContainerUpcomingItems.setVisibility(View.GONE);


                } else {


                    mShimmerViewContainerUpcomingItems.stopShimmer();
                    mShimmerViewContainerUpcomingItems.setVisibility(View.GONE);
                    //Toast.makeText(getActivity(), "No deals available at the moment", Toast.LENGTH_SHORT).show();
                    textViewUpcoming.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onFailure(Call<DealResponse> call, Throwable t) {

            }
        });
    }

    private void setSliderViews(int list) {

        Log.v("listsize2", String.valueOf(list));
        for (int i = 0; i < list; i++) {
            DefaultSliderView sliderView = new DefaultSliderView(getContext());
//            Log.v("sld", String.valueOf(sliderList.size()));
//            Log.v("sld", sliderList.get(i).getSlider().get(i).getId());
//            Log.v("sld", sliderList.get(i).getSlider().get(i).getImgUrl());
//            Log.v("sld", sliderList.get(i).getSlider().get(i).getDescription());
//
            // sliderView.setImageUrl(sliderList.get(i).getSlider().get(i).getImgUrl());
            sliderView.setImageUrl(Img_URL + sliderList.get(i).getSlider().get(i).getImg());
            sliderView.setDescription(sliderList.get(i).getSlider().get(i).getDescription());


//        for (int i = 0; i <= 2; i++) {
//
//            DefaultSliderView sliderView = new DefaultSliderView(getContext());
//
//            switch (i) {
//                case 0:
//                    //sliderView.setImageDrawable(R.drawable.ic_launcher_background);
//                    sliderView.setImageDrawable(R.drawable.slider0);
//                    break;
//                case 1:
//                    sliderView.setImageUrl("https://images.pexels.com/photos/218983/pexels-photo-218983.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
////                    sliderView.setImageDrawable(R.drawable.slider1);
//
//                    //sliderView.setDescription("test 2");
//
//                    break;
//                case 2:
//                    sliderView.setImageDrawable(R.drawable.ic_launcher_background);
//                    sliderView.setImageDrawable(R.drawable.slider2);
//
//                    //sliderView.setDescription("test 3");
//                    break;
//
//            }
            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            final int finalI = i;
            sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(SliderView sliderView) {
                    //Toast.makeText(LandingActivity.this, "This is slider " + (finalI + 1), Toast.LENGTH_SHORT).show();

                }
            });

            //at last add this view in your layout :
            sliderLayout.addSliderView(sliderView);


        }

    }

    public class GridSpacingdecoration extends RecyclerView.ItemDecoration {

        private int span;
        private int space;
        private boolean include;

        public GridSpacingdecoration(int span, int space, boolean include) {
            this.span = span;
            this.space = space;
            this.include = include;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % span;

            if (include) {
                outRect.left = space - column * space / span;
                outRect.right = (column + 1) * space / span;

                if (position < span) {
                    outRect.top = space;
                }
                outRect.bottom = space;
            } else {
                outRect.left = column * space / span;
                outRect.right = space - (column + 1) * space / span;
                if (position >= span) {
                    outRect.top = space;
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmer();

        checkTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        mShimmerViewContainer.stopShimmer();
    }


    private void checkTimer() {
        boolean qrScanned = new QRActivity().getQrScanned();

        if (qrScanned) {
            SharedPreferences prefs = getActivity().getSharedPreferences("time", Context.MODE_PRIVATE);
            long previousTime = prefs.getLong("time", 0);
            long currentTime = new Date().getTime();
            if (currentTime - previousTime > BLOCK_TIME) {
                scanQR.setEnabled(true);
                scanQR.setBackground(mContext.getDrawable(R.drawable.round_buttonredradius));
                //  scanQR.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));

                scanQR.setText("Scan QR");
                //sendNotification("Notification from FoodStudio","You can scan QR now!");

            } else {
                //disable it and start a new CountdownTimer; this is needed in order for
                //it to to become enabled if you're still in the app and the time ran out
                scanQR.setEnabled(false);
                scanQR.setBackground(mContext.getDrawable(R.drawable.round_buttongrey));
                //scanQR.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark2));

                new CountDownTimer(BLOCK_TIME - (currentTime - previousTime), 1000) {
                    public void onTick(long millisUntilFinished) {
                        scanQR.setText("Wait for " + DateUtils.formatElapsedTime(millisUntilFinished / 1000));
//                    timerText.setEndTime((millisUntilFinished / 1000));
                    }

                    public void onFinish() {
                        scanQR.setEnabled(true);
                        scanQR.setBackground(mContext.getDrawable(R.drawable.round_buttonredradius));
                        // scanQR.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                        scanQR.setText("Scan QR");
                        sendNotification("Notification from Food Studio", "You can scan QR now!");
                    }
                }.start();
            }

        } else {
            scanQR.setEnabled(true);
        }


    }

    private void sendNotification(String notificationTitle, String notificationBody) {
        Intent intent = new Intent(mContext, DashboardActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, DEFAULT_CHANNEL_ID)
                .setAutoCancel(true)   //Automatically delete the notification
                .setSmallIcon(R.drawable.fslogo_red) //Notification icon
                .setContentIntent(pendingIntent)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fslogo_red))
                .setSound(defaultSoundUri);


        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

    }

    private void getsliders() {
        Call<SliderResponse> call = RetrofitClient.getInstance().getApi().getSlider();
        call.enqueue(new Callback<SliderResponse>() {
            @Override
            public void onResponse(Call<SliderResponse> call, Response<SliderResponse> response) {
                SliderResponse sliderResponse = response.body();
                if (sliderResponse.getSuccess() != 0) {
                    for (Slider slider : sliderResponse.getSlider()) {
                        Log.v("slider", slider.getId());
                        Log.v("slider", slider.getImg());
                        Log.v("slider", slider.getDescription());
                        sliderList.add(sliderResponse);

                    }
                    listsize = sliderList.size();
                    Log.v("listsize", String.valueOf(listsize));
                    setSliderViews(listsize);
                }

            }

            @Override
            public void onFailure(Call<SliderResponse> call, Throwable t) {

            }
        });

    }
}
