package com.example.drhello.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drhello.R;
import com.example.drhello.model.SliderItem;

import java.util.ArrayList;
public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder>{

    private ArrayList<SliderItem> sliderItems=new ArrayList<>();
    private Context viewPager2;

    public SliderAdapter(ArrayList<SliderItem> sliderItems, Context viewPager2) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.medical_types
                ,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        SliderItem sliderItem=sliderItems.get(position);

        if (sliderItem.getImage()!=0){
            holder.imageView.setImageResource(sliderItem.getImage());
        }
        holder.textView.setText(sliderItem.getImg_name());
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView textView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.img);
            textView=itemView.findViewById(R.id.image_name);
        }
//        void setImage(SliderItem sliderItem){
//
//            imageView.setImageResource(sliderItem.getImage());
//        }
    }


}
