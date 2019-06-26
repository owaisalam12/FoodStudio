
package com.core2plus.oalam.foodstudio.API.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.core2plus.oalam.foodstudio.API.DealResponse;
import com.core2plus.oalam.foodstudio.Entity.Constants;
import com.core2plus.oalam.foodstudio.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import jp.wasabeef.blurry.Blurry;


public class RecycleradapterUpcoming extends RecyclerView.Adapter<RecycleradapterUpcoming.MyHolder> implements View.OnClickListener {
    // TODO: 24-Jun-19 url
//    private static final String BASE_URL="http://192.168.137.1/food/assets/images/Upcoming_items";
    private static final String BASE_URL = Constants.Img_URL_Upcoming;
    // private static final String BASE_URL="http://core2plus.com/food/assets/images/";
    List<DealResponse> list;
    ImageLoader imageLoader;
    private Context context;

    public RecycleradapterUpcoming(List<DealResponse> list, ImageLoader imageLoader) {
        this.list = list;
        this.imageLoader = imageLoader;
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_card2, parent, false);
        MyHolder myHolder = new MyHolder(view);
        context = parent.getContext();
        return myHolder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {

        DealResponse product = list.get(position);

        holder.name.setText(product.getDeals().get(position).getDealsName());
        String image1 = product.getDeals().get(position).getImg();
        // BlurImage.withContext(context).blurFromUri(BASE_URL+image1)
        //       .into(holder.image);
        // imageLoader.displayImage(BASE_URL + image1, holder.image);
        imageLoader.displayImage(BASE_URL + image1, holder.image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                new MyHolder(view).blur();
//                holder.blur();
                //Blurry.with(context).from(loadedImage).into(holder.image);
                Blurry.with(context).radius(25).from(loadedImage).into(holder.image);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {
        Log.d("Recycler", "onClick " + v.toString());
    }


    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, color, price;
        ImageView image;

        //BlurImageView image;
        public MyHolder(View itemView) {
            super(itemView);
            // image = itemView.findViewById(R.id.foodImage);
            image = itemView.findViewById(R.id.foodImage);
            name = itemView.findViewById(R.id.nameCard);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {

            Log.d("Recycler", "onClick " + getAdapterPosition() + " " + list.get(getAdapterPosition()).getDeals().get(getAdapterPosition()).getDealsName());
        }
    }

}
