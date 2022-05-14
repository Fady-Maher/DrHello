package com.example.drhello.ui.mapping;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drhello.adapter.OnSearchPlaceClickListener;
import com.example.drhello.R;

import java.util.ArrayList;

public class SearchMapsAdapter extends RecyclerView.Adapter<SearchMapsAdapter.PlaceSearchMapsHolder> {
    ArrayList<PlaceDetails> placesSearchMap = new ArrayList<>();
    Context context;
    OnSearchPlaceClickListener listener;

    public SearchMapsAdapter(ArrayList<PlaceDetails> placesSearchMap, Context context,OnSearchPlaceClickListener listener) {
        this.placesSearchMap = placesSearchMap;
        this.context = context;
        this.listener = listener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaceSearchMapsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.search_map_item,parent,false);
        return new SearchMapsAdapter.PlaceSearchMapsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceSearchMapsHolder holder, int position) {
        PlaceDetails placesSearchMaps = placesSearchMap.get(position);
        holder.place_name.setText(placesSearchMaps.getPlace_name());
        holder.place_address.setText(placesSearchMaps.getPlace_address());
        holder.place_distance.setText(placesSearchMaps.getDistance());
        holder.time.setText(placesSearchMaps.getTime());
        holder.speed.setText(placesSearchMaps.getSpeed());
    }

    @Override
    public int getItemCount() {
        return placesSearchMap.size();
    }

    public void updateList(ArrayList<PlaceDetails>  list){
        placesSearchMap = list;
        notifyDataSetChanged();
    }

    public class PlaceSearchMapsHolder  extends RecyclerView.ViewHolder{

        TextView place_name,place_address,place_distance,speed,time;

        public PlaceSearchMapsHolder(@NonNull View itemView) {
            super(itemView);
            place_distance=itemView.findViewById(R.id.txt_distance_hospital);
            place_address=itemView.findViewById(R.id.txt_address_hospital);
            place_name=itemView.findViewById(R.id.txt_name_hospital);
            speed=itemView.findViewById(R.id.speed);
            time=itemView.findViewById(R.id.time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int pos = getAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION){
                            listener.onSearchPlaceClick(pos);
                        }
                    }
                }
            });

        }
    }
}
