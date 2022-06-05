package com.example.drhello.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.drhello.R;
import com.example.drhello.model.UserState;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserStateAdapter extends RecyclerView.Adapter<UserStateAdapter.StateHolder> {

    private Context context;
    private ArrayList<UserState>userStates=new ArrayList<>();
    private OnClickFriendStateLinstener onFriendsClickListener;

    public UserStateAdapter() {
    }

    public UserStateAdapter(Context context, ArrayList<UserState> userStates,OnClickFriendStateLinstener onFriendsClickListener) {
        this.context = context;
        this.userStates = userStates;
        this.onFriendsClickListener = onFriendsClickListener;
    }

    @NonNull
    @Override
    public StateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StateHolder(LayoutInflater.from(context).inflate(R.layout.users_state,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull StateHolder holder, int position) {
        UserState userState=userStates.get(position);

        holder.txt_name_user.setText(userState.getUser_name());
        if(userState.getImg_state().equals("Online")){
            holder.ic_state.setImageResource( R.drawable.ic_online);
        }else{
            holder.ic_state.setImageResource( R.drawable.ic_offline);
        }

       try{
            Glide.with(context).load(userState.getImg_user()).placeholder(R.drawable.ic_chat).
                    error(R.drawable.ic_chat).into(holder.img_cur_user);
        }catch (Exception e){
            holder.img_cur_user.setImageResource(R.drawable.ic_chat);
        }

    }

    @Override
    public int getItemCount() {
        return userStates.size();
    }

    public class StateHolder extends RecyclerView.ViewHolder{
        CircleImageView img_cur_user;
        ImageView ic_state;
        TextView txt_name_user;
        public StateHolder(@NonNull View itemView) {
            super(itemView);
            img_cur_user=itemView.findViewById(R.id.img_cur_user);
            ic_state=itemView.findViewById(R.id.ic_state);
            txt_name_user=itemView.findViewById(R.id.txt_name_user);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onFriendsClickListener.onClickState(userStates.get(getAdapterPosition()).getIdfriend());
                }
            });
        }
    }
}
