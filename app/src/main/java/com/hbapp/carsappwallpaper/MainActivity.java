package com.hbapp.carsappwallpaper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private WallpaperAdapter wallpaperAdapter;
    private List<WallpaperModel> wallpaperModelList;
    private WallpaperModel wallpaperModel;

    private InterstitialAd interstitialAd;

    private Boolean isScrolling  = false;
    private int currentItems, totalItems, scrollOutItems;

    // String url ="https://api.pexels.com/v1/curated/?page="+pageNumber+"&per_page=80";
    private String url = "";
    private int pageNumber = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFB();

        initializeAdMob();

        settingsRecyclerView();

        fetchWallpaper();
    }

    private void settingsRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        wallpaperModelList = new ArrayList<>();
        wallpaperAdapter = new WallpaperAdapter(this, wallpaperModelList, new OnItemClickListener() {
            @Override
            public void onItemClick(WallpaperModel item) {
                if (interstitialAd != null) {
                    wallpaperModel = item;
                    interstitialAd.show(MainActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    Intent intent = new Intent(MainActivity.this, FullScreenWallpaper.class);
                    intent.putExtra("originalUrl",item.getOriginalUrl());
                    intent.putExtra("MediumUrl",item.getMediumUrl());
                    intent.putExtra("LargeUrl",item.getMediumUrl());
                    intent.putExtra("large2x",item.getMediumUrl());
                    startActivity(intent);
                }
            }
        });

        recyclerView.setAdapter(wallpaperAdapter);

        final GridLayoutManager gridLayoutManager =
                new GridLayoutManager(this,3,  LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(gridLayoutManager);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(wallpaperModelList.get(position).isPost_ads()  ) {
                    return 3; //item will take 2 column (full row size)
                } else {
                    return 1; //you will have 2 rolumn per row
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling= true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentItems = gridLayoutManager.getChildCount();
                totalItems = gridLayoutManager.getItemCount();
                scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition();

                if(isScrolling && (currentItems+scrollOutItems==totalItems)){
                    isScrolling = false;
                    fetchWallpaper();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        showInterstitialAd();
    }

    private void initializeAdMob() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }

    private void showInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build(); //Initializing the InterstitialAd  objects
        String id_interstitial = getResources().getString(R.string.interstitial_ad_admob);
        InterstitialAd.load(this, id_interstitial, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitial) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                interstitialAd = interstitial;
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d("TAG", "The ad was dismissed.");
                        Intent intent = new Intent(MainActivity.this, FullScreenWallpaper.class);
                        intent.putExtra("item", wallpaperModel.getOriginalUrl());
                        intent.putExtra("MediumUrl", wallpaperModel.getMediumUrl());
                        intent.putExtra("LargeUrl", wallpaperModel.getMediumUrl());
                        intent.putExtra("large2x", wallpaperModel.getMediumUrl());
                        startActivity(intent);
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {

                        interstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i("TAG", loadAdError.getMessage());
                interstitialAd = null;
            }
        });
    }

    private void initializeFB() {

        // fb mobile id
        AudienceNetworkAds.initialize(this);
        AdSettings.addTestDevice("b5e25e24-bc81-43c8-895d-2431c801fb90");

        com.facebook.ads.AdView adViewFb = new com.facebook.ads.AdView(this, "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID", AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = findViewById(R.id.banner_container);
        adContainer.addView(adViewFb);
        adViewFb.loadAd();
    }

    public void fetchWallpaper(){

        url = "https://api.pexels.com/v1/search/?page="+pageNumber+ "&per_page=50&query=car";
        StringRequest request = new StringRequest(Request.Method.GET,url ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                      try{
                          JSONObject jsonObject = new JSONObject(response);
                          JSONArray jsonArray= jsonObject.getJSONArray("photos");
                          int length = jsonArray.length();
                          for(int i=0; i<length; i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            int id = object.getInt("id");
                            JSONObject objectImages = object.getJSONObject("src");
                            String orignalUrl = objectImages.getString("original");
                            String mediumUrl = objectImages.getString("medium");
                            String largeUrl = objectImages.getString("large");
                            String large2x = objectImages.getString("large2x");

                            WallpaperModel wallpaperModel = new WallpaperModel(id, orignalUrl, mediumUrl, largeUrl, large2x);
                            if(i == 9 ){
                                WallpaperModel wallpaperModelAds = new WallpaperModel();
                                wallpaperModelAds.setPost_ads(true);
                                wallpaperModelList.add(wallpaperModelAds);
                            }
                                wallpaperModelList.add(wallpaperModel);
                          }

                          wallpaperAdapter.notifyDataSetChanged();
                          pageNumber++;
                          Log.i("onURLResponse  ", pageNumber+"  : "+ url);

                      }catch (JSONException ignored){

                      }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization","563492ad6f917000010000016c9a3b786a2749c6a6cdcf1fd3cbe405");

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);

    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.nav_search){

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            final EditText editText = new EditText(this);
            editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            alert.setMessage("Enter Category e.g. Nature");
            alert.setTitle("Search Wallpaper");

            alert.setView(editText);

            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    String query = editText.getText().toString().toLowerCase();

                    url = "https://api.pexels.com/v1/search/?page="+pageNumber+"&per_page=80&query="+query;
                   // url = "https://api.pexels.com/v1/search/?page="+pageNumber+"&per_page=80&query=paris";
                    wallpaperModelList.clear();
                    fetchWallpaper();

                }
            });

            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }
*/
}