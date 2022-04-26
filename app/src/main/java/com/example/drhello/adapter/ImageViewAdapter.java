package com.example.drhello.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.drhello.R;
import com.google.firebase.storage.FirebaseStorage;
import java.util.ArrayList;
import java.util.List;

public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.ImageViewHolder>{
    private Context context;
    private List<String> imgUri=new ArrayList<>();
    private String imgId;
    private FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
    private OnPostClickListener onPostClickListener;
    public ImageViewAdapter(Context context, List<String> imgUri,String imgId , OnPostClickListener onPostClickListener) {
        this.context = context;
        this.imgUri = imgUri;
        this.imgId=imgId;
        this.onPostClickListener = onPostClickListener;
    }

    public ImageViewAdapter() {
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.post_image,parent,false);
        return new ImageViewHolder(view, onPostClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imagePath=imgUri.get(position);
        if (!imagePath.isEmpty())
        {
            Glide.with(context)
                    .load(imagePath)
                    .placeholder(R.drawable.hospital).into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return imgUri.size();
    }

    public class ImageViewHolder  extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ImageViewHolder(@NonNull View itemView, OnPostClickListener onPostClickListener) {
            super(itemView);
            imageView=itemView.findViewById(R.id.img_select);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPostClickListener.onClickImage(imgUri.get(getAdapterPosition()));
                }
            });
        }
    }
}
