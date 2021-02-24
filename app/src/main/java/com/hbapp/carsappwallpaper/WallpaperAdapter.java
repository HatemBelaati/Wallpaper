package com.hbapp.carsappwallpaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<WallpaperModel> wallpaperModelList;
    private final OnItemClickListener listener;

    public WallpaperAdapter(Context context, List<WallpaperModel> wallpaperModelList, OnItemClickListener listener) {
        this.context = context;
        this.wallpaperModelList = wallpaperModelList;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (wallpaperModelList.get(position).isPost_ads()){
            return 1;
        }else {
            return 0;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == 0){
            View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent,false);
            return new WallpaperViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.pub_item, parent,false);
            return new AdsPostHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if(wallpaperModelList.get(position).isPost_ads()){
            AdsPostHolder ads = (AdsPostHolder) holder;
            AdRequest adRequest = new AdRequest.Builder().build();
            ads.adView.loadAd(adRequest);
        }else {
            WallpaperViewHolder wallpaper = (WallpaperViewHolder) holder;
            Glide.with(context).load(wallpaperModelList.get(position).getMediumUrl()).into(wallpaper.imageView);
            wallpaper.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(wallpaperModelList.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return wallpaperModelList.size();
    }
}

class WallpaperViewHolder extends RecyclerView.ViewHolder{
    ImageView imageView;
    public WallpaperViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView=itemView.findViewById(R.id.imageViewItem);
    }
}

class AdsPostHolder extends RecyclerView.ViewHolder {
    AdView adView;

    AdsPostHolder(@NonNull View itemView) {
        super(itemView);
        this.adView =  itemView.findViewById(R.id.adView);

    }
}
