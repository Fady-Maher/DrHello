package com.example.drhello.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.drhello.model.AddPersonModel;
import com.example.drhello.R;
import com.example.drhello.model.ReactionModel;
import com.example.drhello.model.UserAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class NumReactionAdapter extends RecyclerView.Adapter<NumReactionAdapter.ReactionsViewHolder> {
    Context context;
    ArrayList<ReactionModel> reactionModels = new ArrayList<>();

    public NumReactionAdapter(Context context, ArrayList<ReactionModel> reactionModels) {
        this.context = context;
        this.reactionModels = reactionModels;
    }

    @NonNull
    @Override
    public NumReactionAdapter.ReactionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NumReactionAdapter.
                ReactionsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.itemnumreaction, parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReactionsViewHolder holder, int position) {
        ReactionModel reactionModel = reactionModels.get(position);
        holder.name_user.setText(reactionModel.getName_user());
        // holder.reaction.setImageResource(reactionModel.getReaction());

        try {
            Glide.with(context).load(reactionModel.getImg_user()).placeholder(R.drawable.ic_chat).
                    error(R.drawable.ic_chat).into(holder.img_user);
        } catch (Exception e) {
            holder.img_user.setImageResource(R.drawable.ic_chat);
        }

        switch (reactionModel.getReaction()) {
            case "Like":
                holder.reaction.setImageResource(R.drawable.ic_like);
                break;
            case "Love":
                holder.reaction.setImageResource(R.drawable.ic_love);
                break;
            case "Haha":
                holder.reaction.setImageResource(R.drawable.ic_haha);
                break;
            case "Sad":
                holder.reaction.setImageResource(R.drawable.ic_sad);
                break;
            case "Wow":
                holder.reaction.setImageResource(R.drawable.ic_wow);
                break;
            case "Angry":
                holder.reaction.setImageResource(R.drawable.ic_angry);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return reactionModels.size();
    }

    public class ReactionsViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView img_user;
        private TextView name_user;
        private ImageView reaction;

        public ReactionsViewHolder(@NonNull View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.img_cur_user);
            name_user = itemView.findViewById(R.id.txt_name_user);
            reaction = itemView.findViewById(R.id.ic_reaction);
        }
    }
}