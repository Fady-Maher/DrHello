package com.example.drhello;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.drhello.model.SliderItem;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SpecialistAdapter  extends RecyclerView.Adapter<SpecialistAdapter.SpecialistViewHolder> {
    private Context context;
    private ArrayList<SliderItem> sliderItemArrayList = new ArrayList<>();
    private OnClickDoctorInterface onClickDoctorInterface;

    public SpecialistAdapter(Context context , ArrayList<SliderItem> sliderItemArrayList,OnClickDoctorInterface onClickDoctorInterface) {
        this.context = context;
        this.sliderItemArrayList = sliderItemArrayList;
        this.onClickDoctorInterface = onClickDoctorInterface;
    }

    @NonNull
    @Override
    public SpecialistAdapter.SpecialistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SpecialistAdapter.SpecialistViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_specialist, parent,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialistAdapter.SpecialistViewHolder holder, int position) {
        SliderItem sliderItem = sliderItemArrayList.get(position);

        holder.name.setText(sliderItem.getImg_name());
        holder.img.setImageResource(sliderItem.getImage());
    }

    @Override
    public int getItemCount() {
        return sliderItemArrayList.size();
    }

    public class SpecialistViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView img;
        private TextView name;
        public SpecialistViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.txt);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickDoctorInterface.OnClick(sliderItemArrayList.get(getAdapterPosition()).getImg_name());
                }
            });
        }
    }
}