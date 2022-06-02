package com.example.drhello.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.drhello.R;
import com.example.drhello.model.UserAccount;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorAdapter  extends RecyclerView.Adapter<DoctorAdapter.DoctorInfoViewHolder> {
    private Context context;
    private ArrayList<UserAccount> addPersonAdapterArrayList = new ArrayList<>();
    private OnDoctorsClickLinstener onDoctorsClickLinstener;

    public DoctorAdapter(Context context,ArrayList<UserAccount> addPersonAdapterArrayList,OnDoctorsClickLinstener onDoctorsClickLinstener) {
        this.context = context;
        this.addPersonAdapterArrayList = addPersonAdapterArrayList;
        this.onDoctorsClickLinstener = onDoctorsClickLinstener;
    }

    @NonNull
    @Override
    public DoctorAdapter.DoctorInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DoctorAdapter.DoctorInfoViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.doctor_item, parent,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorAdapter.DoctorInfoViewHolder holder, int position) {
        UserAccount userAccount = addPersonAdapterArrayList.get(position);

        holder.name_user.setText(userAccount.getName());
        holder.txt_spec.setText(userAccount.getUserInformation().getSpecification_in());

        try{
            Glide.with(context).load(userAccount.getImg_profile()).placeholder(R.drawable.ic_chat).
                    error(R.drawable.ic_chat).into(holder.img_user);
        }catch (Exception e){
            holder.img_user.setImageResource(R.drawable.ic_chat);
        }

    }

    @Override
    public int getItemCount() {
        return addPersonAdapterArrayList.size();
    }

    public class DoctorInfoViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView img_user;
        private TextView name_user,txt_spec;
        private LinearLayout ln_call,ln_chat,ln_place;
        public DoctorInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.img_cur_user);
            name_user = itemView.findViewById(R.id.txt_name_user);
            txt_spec = itemView.findViewById(R.id.spec);
            ln_call = itemView.findViewById(R.id.ln_call);
            ln_chat = itemView.findViewById(R.id.ln_chat);
            ln_place = itemView.findViewById(R.id.ln_place);
            ln_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDoctorsClickLinstener.OnClickCall(getAdapterPosition(),addPersonAdapterArrayList.get(getAdapterPosition()).getUserInformation().getPhone());
                }
            });

            ln_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDoctorsClickLinstener.OnClickChat(getAdapterPosition(),addPersonAdapterArrayList.get(getAdapterPosition()));
                }
            });

            ln_place.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDoctorsClickLinstener.OnClickPlace(getAdapterPosition(),addPersonAdapterArrayList.get(getAdapterPosition()).getUserInformation().getAddress_work());
                }
            });
        }
    }
}
