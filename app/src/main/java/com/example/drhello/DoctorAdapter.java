package com.example.drhello;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.drhello.model.UserAccount;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorAdapter  extends RecyclerView.Adapter<DoctorAdapter.DoctorInfoViewHolder> {
    private Context context;
    private ArrayList<UserAccount> addPersonAdapterArrayList = new ArrayList<>();

    public DoctorAdapter(Context context,ArrayList<UserAccount> addPersonAdapterArrayList) {
        this.context = context;
        this.addPersonAdapterArrayList = addPersonAdapterArrayList;
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
        public DoctorInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.img_cur_user);
            name_user = itemView.findViewById(R.id.txt_name_user);
            txt_spec = itemView.findViewById(R.id.spec);
        }
    }
}
