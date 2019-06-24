package com.core2plus.oalam.foodstudio.Adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.core2plus.oalam.foodstudio.API.Purchase;
import com.core2plus.oalam.foodstudio.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;


/**
 * Created by zhakhanyergali on 08.11.17.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    // Variables
    //private List<History> historyList;
    private List<Purchase> purchaseList;
    private Context context;
    ImageLoader imageLoader = ImageLoader.getInstance();

    //    public HistoryAdapter(List<History> historyList) {
//        this.historyList = historyList;
//    }
    public HistoryAdapter(List<Purchase> purchaseList) {
        this.purchaseList = purchaseList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = null;

        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item_img, parent, false);

        ViewHolder vh = new ViewHolder(v);
        context = parent.getContext();
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //String QRtext=purchaseList.get(position).getContext();
//        String[] split=QRtext.split(";");
//        Log.v("split0",split[0]);
//        Log.v("split1",split[1]);
        // holder.context.setText((position + 1) + ". " + historyList.get(position).getContext());
        holder.context.setText((position + 1) + ". " + purchaseList.get(position).getText());
        //imageLoader.displayImage(historyList.get(position).getContext(), holder.img);
        imageLoader.displayImage(purchaseList.get(position).getURl(), holder.img);

//        holder.date.setText(purchaseList.get(position).getDate());
        holder.date.setText(purchaseList.get(position).getPurchaseTime());
        holder.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url;
                if (Patterns.WEB_URL.matcher(purchaseList.get(position).getImgUrl()).matches()) {
                    url = purchaseList.get(position).getImgUrl();
                } else {
                    url = "http://www.google.com/#q=" + purchaseList.get(position).getImgUrl();
                }


                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            }
        });
        holder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", purchaseList.get(position).getImgUrl());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = purchaseList.get(position).getImgUrl();
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                context.startActivity(Intent.createChooser(sharingIntent, "Share "));

            }
        });
    }

    @Override
    public int getItemCount() {
        //return historyList.size();
        return purchaseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView context, date;
        public ImageView search, copy, share, img;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.findViewById(R.id.contextTextView);
            img = itemView.findViewById(R.id.imgUrl);
            date = itemView.findViewById(R.id.dateTextView);
            search = itemView.findViewById(R.id.searchImageView);
            copy = itemView.findViewById(R.id.copyImageView);
            share = itemView.findViewById(R.id.shareImageView);
        }
    }

}
