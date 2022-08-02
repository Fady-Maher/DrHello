package com.example.drhello.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class AddPersonAdapter  extends RecyclerView.Adapter<AddPersonAdapter.AddPersonViewHolder> {
    private Context context;
    private ArrayList<UserAccount> addPersonAdapterArrayList = new ArrayList<>();
    private OnClickAddPersonListener onClickAddPersonListener;
    private UserAccount currentAccount;


    public AddPersonAdapter(Context context,ArrayList<UserAccount> addPersonAdapterArrayList
            ,OnClickAddPersonListener onClickAddPersonListener,UserAccount currentAccount) {
        this.context = context;
        this.addPersonAdapterArrayList = addPersonAdapterArrayList;
        this.onClickAddPersonListener = onClickAddPersonListener;
        this.currentAccount=currentAccount;
        Log.e("AddPerson : " , addPersonAdapterArrayList.size()+"");
    }

    @NonNull
    @Override
    public AddPersonAdapter.AddPersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddPersonAdapter.AddPersonViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_add_person, parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddPersonAdapter.AddPersonViewHolder holder, int position) {
        UserAccount userAccount = addPersonAdapterArrayList.get(position);

        if (currentAccount.getRequestSsent().containsKey(userAccount.getId())){
            holder.btn_add.setVisibility(View.GONE);
            holder.btn_cancel.setVisibility(View.VISIBLE);
        }else {
            holder.btn_cancel.setVisibility(View.GONE);
            holder.btn_add.setVisibility(View.VISIBLE);
        }

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
        return addPersonAdapterArrayList.size();
    }

    public class AddPersonViewHolder extends RecyclerView.ViewHolder {
        private  CircleImageView img_user;
        private TextView name_user;
        private Button btn_add,btn_cancel;
        public AddPersonViewHolder(@NonNull View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.img_cur_user);
            name_user = itemView.findViewById(R.id.txt_name_user);
            btn_add = itemView.findViewById(R.id.btn_add);
            btn_cancel=itemView.findViewById(R.id.btn_cancel);


            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(CheckNetwork.getConnectivityStatusString(context) == 1) {
                        onClickAddPersonListener.onClick(addPersonAdapterArrayList.get(getAdapterPosition()),"add");
                        btn_cancel.setVisibility(View.VISIBLE);
                        btn_add.setVisibility(View.GONE);
                    }else{
                        Toast.makeText(context, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(CheckNetwork.getConnectivityStatusString(context) == 1) {
                        onClickAddPersonListener.onClick(addPersonAdapterArrayList.get(getAdapterPosition()),"cancel");
                        btn_add.setVisibility(View.VISIBLE);
                        btn_cancel.setVisibility(View.GONE);
                    }else{
                        Toast.makeText(context, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }
}