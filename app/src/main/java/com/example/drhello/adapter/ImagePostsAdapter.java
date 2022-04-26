package com.example.drhello.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drhello.R;

import java.util.ArrayList;
import java.util.List;

public class ImagePostsAdapter extends RecyclerView.Adapter<ImagePostsAdapter.ImagePostsHolder>{
    private Context context;
    private List<Bitmap> bitmaps=new ArrayList<>();

    public ImagePostsAdapter() {
    }

    public ImagePostsAdapter(Context context, List<Bitmap> bitmaps) {
        this.context = context;
        this.bitmaps = bitmaps;
    }

    @NonNull
    @Override
    public ImagePostsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.post_image,parent,false);
        return new ImagePostsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagePostsHolder holder, int position) {
        Bitmap bitmap=bitmaps.get(position);
        holder.imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return bitmaps.size();
    }

    public class ImagePostsHolder  extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ImagePostsHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.img_select);
        }
    }
}
