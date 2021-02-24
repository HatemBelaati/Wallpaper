package com.hbapp.carsappwallpaper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;


public class FullScreenWallpaper extends AppCompatActivity {

    private String originalUrl="";
    private String mediumUrl="";
    private String largeUrl="";
    private String large2x="";
    private PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_wallpaper);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        getSupportActionBar().hide();
        Intent intent =  getIntent();

        originalUrl = intent.getStringExtra("originalUrl");
        large2x = intent.getStringExtra("large2x");
        largeUrl = intent.getStringExtra("LargeUrl");
        mediumUrl = intent.getStringExtra("MediumUrl");

        photoView = findViewById(R.id.photoView);
        //Glide.with(this).load(originalUrl).into(photoView);
        Glide.with(this).load(large2x).into(photoView);
    }

    public void SetWallpaperEvent(View view) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Bitmap bitmap  = ((BitmapDrawable)photoView.getDrawable()).getBitmap();
        try {
            wallpaperManager.setBitmap(bitmap);
            Toast.makeText(this, "Wallpaper Set", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void DownloadWallpaperEvent(View view) {
        DownloadManager downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(large2x);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadManager.enqueue(request);
        Toast.makeText(this, "Downloading Start", Toast.LENGTH_SHORT).show();
    }

}