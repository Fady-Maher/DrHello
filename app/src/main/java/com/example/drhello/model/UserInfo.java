package com.example.drhello.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.drhello.R;
import com.example.drhello.ui.main.DrawerAdapter;
import com.example.drhello.ui.main.DrawerItem;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfo extends DrawerItem<UserInfo.ViewHolder> {
    private final String nameUser;
    private final String imgUser;
    private final Context context;


    public UserInfo(String nameUser, String imgUser, Context context) {
        this.nameUser = nameUser;
        this.imgUser = imgUser;
        this.context = context;
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View v=inflater.inflate(R.layout.user_info,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void bindViewHolder(ViewHolder holder) {
        holder.nameUser.setText(nameUser);
        if (imgUser != null){
            Glide.with(context).load(imgUser).placeholder(R.drawable.user).into(holder.imgUser);
        }else {
            holder.imgUser.setImageResource(R.drawable.user);
        }
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    public static class ViewHolder extends DrawerAdapter.ViewHolder{

        private final TextView nameUser;
        private final CircleImageView imgUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameUser=itemView.findViewById(R.id.name_cur_user);
            imgUser=itemView.findViewById(R.id.img_cur_user);
        }
    }
}
