package com.example.drhello.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.drhello.R;
import com.example.drhello.ui.news.NewsModel;

import java.util.ArrayList;
import java.util.Objects;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    ArrayList<NewsModel> newsModelList = new ArrayList<>();
    OnNewsClickListener onNewsClickListener;
    Context context;

    public void setOnNewsClickListener(OnNewsClickListener onNewsClickListener) {
        this.onNewsClickListener = onNewsClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setNews(ArrayList<NewsModel> newsModelList , Context context) {
        this.newsModelList = newsModelList;
        notifyDataSetChanged();
        this.context = context;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false),onNewsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsModel newsList = newsModelList.get(position);
        holder.title.setText(newsList.getTitle());
        holder.description.setText(newsList.getDescription());
        holder.publishDate.setText(newsList.getDate());
        if(newsList.getImage() == null) {
            Glide.with(context).load(newsList.getImageUrl()).placeholder(R.drawable.no_image).error(R.drawable.erorr).into(holder.articleImage);
        }else{
            Glide.with(context).load(newsList.getImage()).placeholder(R.drawable.no_image).error(R.drawable.erorr).into(holder.articleImage);
        }

        if(!isNetworkAvailable()){
            holder.share.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return newsModelList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        private final ImageView articleImage;
        private final TextView title;
        private final TextView description;
        private final TextView publishDate;
        private final ImageButton share;
        public NewsViewHolder(@NonNull View itemView , OnNewsClickListener listener) {
            super(itemView);
            articleImage = itemView.findViewById(R.id.ivArticleImage);
            title = itemView.findViewById(R.id.tvTitle);
            description = itemView.findViewById(R.id.tvDescription);
            publishDate = itemView.findViewById(R.id.publish_date);
            share = itemView.findViewById(R.id.share_news_btn);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int pos = getAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION){
                            listener.onNewsClick(pos);
                        }
                    }
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int pos = getAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION){
                            listener.onShareClick(pos);
                        }
                    }
                }
            });
        }

    }

    private boolean isNetworkAvailable() {
        @SuppressLint("UseRequireInsteadOfGet") ConnectivityManager connectivityManager
                = (ConnectivityManager) Objects.requireNonNull(context).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
