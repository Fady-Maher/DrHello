package com.example.drhello.ui.news;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drhello.R;
import com.example.drhello.adapter.NewsAdapter;
import com.example.drhello.adapter.OnNewsClickListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainNewsFragment extends Fragment {

    private RecyclerView recyclerView ;
    private NewsViewModel newsViewModel;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;
    private static final String TAG = "Main";

    public MainNewsFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("UseRequireInsteadOfGet")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main_news, container, false);
        recyclerView = view.findViewById(R.id.rv_news);

        progressBar = view.findViewById(R.id.news_progressBar);
        newsViewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
        if(isNetworkAvailable())
        {
            newsViewModel.getNews();
            newsViewModel.deleteNews(getContext());
            setRecyclerView();
            newsViewModel.newsMutableLiveData.observe(Objects.requireNonNull(getActivity()), newsModels -> {
                newsAdapter.setNews((ArrayList<NewsModel>) newsModels,getContext());
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "onChanged: " + newsModels.get(0).getImage());
                newsViewModel.insertNewsModelOffline(getContext(),newsModels);
                setWebPage(newsModels,view);
            });

        }
        else {
            //Snackbar.make(view.findViewById(R.id.newsConstraintLayout),"No Internet!",Snackbar.LENGTH_SHORT).show();
            newsViewModel.getNewsOffline(getContext());
            setRecyclerView();
            newsViewModel.newsMutableLiveData.observe(Objects.requireNonNull(getActivity()), newsModels -> {
                newsAdapter.setNews((ArrayList<NewsModel>) newsModels,getContext());
                progressBar.setVisibility(View.GONE);
            });
        }

        return view;
    }


    private void setWebPage(List<NewsModel> list,View view){
        newsAdapter.setOnNewsClickListener(new OnNewsClickListener() {
            @Override
            public void onNewsClick(int pos) {
                if (isNetworkAvailable()) {
                    Intent intent = new Intent(getContext(),WebViewActivity.class);
                    intent.putExtra("url",list.get(pos).getUrl());
                    startActivity(intent);
                }else{
                    Snackbar.make(view.findViewById(R.id.newsConstraintLayout),"No Internet!",Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onShareClick(int pos) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, list.get(pos).getUrl());
                startActivity(Intent.createChooser(sendIntent, null));
            }
        });
    }
    private void setRecyclerView(){
        newsAdapter = new NewsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(newsAdapter);
    }

    private boolean isNetworkAvailable() {
        @SuppressLint("UseRequireInsteadOfGet") ConnectivityManager connectivityManager
                = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

