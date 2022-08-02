package com.example.drhello.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.drhello.R;
import com.example.drhello.model.LastChat;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.SearchUserViewHolder>{
    private Context context;
    private ArrayList<LastChat> userAccountArrayList = new ArrayList<>();
    private OnFriendsClickListener onFriendsClickListener;

    public SearchUserAdapter(Context context , ArrayList<LastChat> userAccountArrayList
            ,OnFriendsClickListener onFriendsClickListener){
        this.context = context;
        this.userAccountArrayList = userAccountArrayList;
        this.onFriendsClickListener = onFriendsClickListener;
    }

    @NonNull
    @Override
    public SearchUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchUserViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_user, parent,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchUserViewHolder holder, int position) {
        LastChat lastChat = userAccountArrayList.get(position);

        holder.name_user.setText(lastChat.getNameSender());
        try{
            Glide.with(context).load(lastChat.getImage_person()).placeholder(R.drawable.ic_chat).
                    error(R.drawable.ic_chat).into(holder.img_user);
        }catch (Exception e){
            holder.img_user.setImageResource(R.drawable.ic_chat);
        }
    }

    @Override
    public int getItemCount() {
        return userAccountArrayList.size();
    }

    public void setDate(ArrayList<LastChat> userAccountArrayList){
        this.userAccountArrayList = userAccountArrayList;
        notifyDataSetChanged();
    }

    public class SearchUserViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView img_user;
        private TextView name_user;
        public SearchUserViewHolder(@NonNull View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.img_cur_user);
            name_user = itemView.findViewById(R.id.txt_name_user);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onFriendsClickListener.onClick(userAccountArrayList.get(getAdapterPosition()));
                }
            });
        }
    }
}
