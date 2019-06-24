package com.core2plus.oalam.foodstudio.API.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chootdev.blurimg.BlurImage;
import com.core2plus.oalam.foodstudio.API.DealResponse;
import com.core2plus.oalam.foodstudio.Entity.Constants;
import com.core2plus.oalam.foodstudio.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;


public class Recycleradapter extends RecyclerView.Adapter<Recycleradapter.MyHolder> implements View.OnClickListener {
    // TODO: 24-Jun-19 url
//    private static final String BASE_URL="http://192.168.137.1/food/assets/images/food_image";
    private static final String BASE_URL= Constants.Img_URL_Available;
   // private static final String BASE_URL="http://core2plus.com/food/assets/images/";
    List<DealResponse> list;
    ImageLoader imageLoader;
    private Context context;
    public Recycleradapter(List<DealResponse> list, ImageLoader imageLoader) {
        this.list = list;
        this.imageLoader = imageLoader;
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_card2, parent, false);
        MyHolder myHolder = new MyHolder(view);
        context=parent.getContext();
        return myHolder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        DealResponse product = list.get(position);
        holder.name.setText(product.getDeals().get(position).getDealsName());
        String image1 = product.getDeals().get(position).getImg();
       // BlurImage.withContext(context).blurFromUri(BASE_URL+image1)
         //       .into(holder.image);
        imageLoader.displayImage(BASE_URL+image1, holder.image);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {
        Log.d("Recycler", "onClick " + v.toString());
    }

    class MyHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        TextView name, color, price;
        ImageView image;

        public MyHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.foodImage);
            name = itemView.findViewById(R.id.nameCard);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {

            Log.d("Recycler", "onClick " + getAdapterPosition()+" "+list.get(getAdapterPosition()).getDeals().get(getAdapterPosition()).getDealsName());
        }
    }

}
