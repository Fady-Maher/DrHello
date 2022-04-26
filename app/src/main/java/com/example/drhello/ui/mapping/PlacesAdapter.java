package com.example.drhello.ui.mapping;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drhello.adapter.OnPlaceClickListener;
import com.example.drhello.R;

import java.util.ArrayList;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlaceHolder> {

    ArrayList<PlaceDetails> placeDetails=new ArrayList<>();
    Context context;
    OnPlaceClickListener listener;

    public PlacesAdapter() {
    }

    @SuppressLint("NotifyDataSetChanged")
    public PlacesAdapter(ArrayList<PlaceDetails> placeDetails, Context context, OnPlaceClickListener listener) {
        this.placeDetails = placeDetails;
        this.context = context;
        this.listener = listener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.places_design,parent,false);
        return new PlaceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceHolder holder, int position) {
        PlaceDetails placeDetail=placeDetails.get(position);
        holder.place_img.setImageResource(placeDetail.getPlace_img());
        holder.place_name.setText(placeDetail.getPlace_name());
        holder.place_loc.setText(placeDetail.getPlace_loc());
        holder.car_min.setText(placeDetail.getDistance());
        holder.walk_min.setText(placeDetail.getTime());
        holder.bike_min.setText(placeDetail.getSpeed());
    }

    @Override
    public int getItemCount() {
        return placeDetails.size();
    }

    public class PlaceHolder  extends RecyclerView.ViewHolder{
        ImageView place_img;
        TextView place_loc,place_name,car_min,walk_min,bike_min;
        public PlaceHolder(@NonNull View itemView) {
            super(itemView);
            place_img=itemView.findViewById(R.id.place_img);
            place_loc=itemView.findViewById(R.id.place_loc);
            place_name=itemView.findViewById(R.id.place_name);
            car_min=itemView.findViewById(R.id.car_min);
            bike_min=itemView.findViewById(R.id.bike_min);
            walk_min=itemView.findViewById(R.id.walk_min);

            itemView.setOnClickListener(view -> {
                if(listener!=null){
                    int pos = getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        listener.onPlaceClick(pos);
                    }
                }
            });
        }
    }
}
