package com.example.drhello.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.drhello.R;
import com.example.drhello.connectionnewtwork.CheckNetwork;
import com.example.drhello.model.UserAccount;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class RequestsPersonAdapter  extends RecyclerView.Adapter<RequestsPersonAdapter.RequestsPersonViewHolder> {
    Context context;
    ArrayList<UserAccount> RequestsPersonAdapterArrayList = new ArrayList<>();
    OnClickRequestsPersonListener onClickRequestsPersonListener;

    public RequestsPersonAdapter(Context context,ArrayList<UserAccount> RequestsPersonAdapterArrayList
            ,OnClickRequestsPersonListener onClickRequestsPersonListener) {
        this.context = context;
        this.RequestsPersonAdapterArrayList = RequestsPersonAdapterArrayList;
        this.onClickRequestsPersonListener = onClickRequestsPersonListener;
        Log.e("RequestsPerson : " , RequestsPersonAdapterArrayList.size()+"");
    }

    @NonNull
    @Override
    public RequestsPersonAdapter.RequestsPersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestsPersonAdapter.RequestsPersonViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request_person, parent,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsPersonAdapter.RequestsPersonViewHolder holder, int position) {
        UserAccount userAccount = RequestsPersonAdapterArrayList.get(position);
        holder.name_user.setText(userAccount.getName());
        try{
            Glide.with(context).load(userAccount.getImg_profile()).placeholder(R.drawable.ic_chat).
                    error(R.drawable.ic_chat).into(holder.img_user);
        }catch (Exception e){
            holder.img_user.setImageResource(R.drawable.ic_chat);
        }
    }

    @Override
    public int getItemCount() {
        return RequestsPersonAdapterArrayList.size();
    }

    public class RequestsPersonViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView img_user;
        private TextView name_user;
        private ImageButton btn_accept,btn_delete;
        public RequestsPersonViewHolder(@NonNull View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.img_cur_user);
            name_user = itemView.findViewById(R.id.txt_name_user);
            btn_accept = itemView.findViewById(R.id.btn_accept);
            btn_delete = itemView.findViewById(R.id.btn_delete);

            btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(CheckNetwork.getConnectivityStatusString(context) == 1) {
                        onClickRequestsPersonListener.onClickAccept(RequestsPersonAdapterArrayList.get(getAdapterPosition()));
                    }else{
                        Toast.makeText(context, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(CheckNetwork.getConnectivityStatusString(context) == 1) {
                        onClickRequestsPersonListener.onClickDelete(RequestsPersonAdapterArrayList.get(getAdapterPosition()));
                    }else{
                        Toast.makeText(context, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
