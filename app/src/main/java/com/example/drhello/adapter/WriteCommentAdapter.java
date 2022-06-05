package com.example.drhello.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.drhello.connectionnewtwork.CheckNetwork;
import com.example.drhello.ui.writepost.FBReactionDialog;
import com.example.drhello.model.CommentModel;
import com.example.drhello.R;
import com.example.drhello.ui.writepost.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.skyhope.showmoretextview.ShowMoreTextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class WriteCommentAdapter  extends RecyclerView.Adapter<WriteCommentAdapter.CommentsHolder>{
    private Context context;
    private ArrayList<CommentModel> commentModels = new ArrayList<>();
    private OnCommentClickListener onCommentClickListener;
    private String reactionType2 = "0";
    private FragmentManager fragmentManager;
    public static ProgressDialog mProgress;
    private String type ;


    public WriteCommentAdapter(Context context, ArrayList<CommentModel> commentModels
            ,OnCommentClickListener onCommentClickListener,FragmentManager fragmentManager,String type) {
        this.context = context;
        this.commentModels = commentModels;
        this.onCommentClickListener = onCommentClickListener;
        this.fragmentManager = fragmentManager;
        this.type =type;
        mProgress = new ProgressDialog(context);
    }

    @NonNull
    @Override
    public CommentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.comment_design,parent,false);



        return new WriteCommentAdapter.CommentsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsHolder holder, int position) {
        CommentModel commentModel = commentModels.get(position);


        holder.user_name.setText(commentModel.getUser_name());
        holder.name_only.setText(commentModel.getUser_name());
        holder.numreaction.setText(commentModel.getReactions().size()+"");

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.US);
        TimeAgo timeAgo = new TimeAgo();

        try {
            Date date = dateFormat.parse(commentModel.getDate());
        //    Log.e("getTime : ",date.getTime()+"");
            holder.txt_date.setText(timeAgo.getTimeAgo(date.getTime(),false));
        } catch (ParseException e) {
         //   Log.e("getTime : ",e.getMessage());
            e.printStackTrace();
        }

        holder.comment.setVisibility(View.GONE);
        holder.card_comment.setVisibility(View.GONE);
        holder.card_image.setVisibility(View.GONE);

        if(commentModel.getComment().isEmpty() && commentModel.getComment_image() != null){
            try{
                Glide.with(context).load(commentModel.getComment_image()).placeholder(R.drawable.ic_chat).
                        error(R.drawable.ic_chat).into(holder.image_comment);
            }catch (Exception e){
                holder.image_comment.setImageResource(R.drawable.ic_chat);
            }

            holder.card_comment.setVisibility(View.GONE);
            holder.name_only.setVisibility(View.VISIBLE);
            holder.card_image.setVisibility(View.VISIBLE);
        }else if(commentModel.getComment() !=null && commentModel.getComment_image() ==null){
            holder.card_comment.setVisibility(View.VISIBLE);
            holder.name_only.setVisibility(View.GONE);
            holder.card_image.setVisibility(View.GONE);
            holder.comment.setVisibility(View.VISIBLE);
            holder.comment.setText(commentModel.getComment());
            holder.comment.setShowingChar(100);
            holder.comment.setShowingLine(5);
            holder.comment.setShowMoreColor(context.getResources().getColor(R.color.appColor));
            holder.comment.setShowLessTextColor(context.getResources().getColor(R.color.red));
            holder.comment.addShowMoreText("More");
            holder.comment.addShowLessText("Less");

        }else{
            try{
                Glide.with(context).load(commentModel.getComment_image()).placeholder(R.drawable.ic_chat).
                        error(R.drawable.ic_chat).into(holder.image_comment);
            }catch (Exception e){
                holder.image_comment.setImageResource(R.drawable.ic_chat);
            }

            holder.name_only.setVisibility(View.GONE);
            holder.card_image.setVisibility(View.VISIBLE);
            holder.card_comment.setVisibility(View.VISIBLE);
            holder.comment.setVisibility(View.VISIBLE);
            holder.comment.setText(commentModel.getComment());
            holder.comment.setShowingChar(100);
            holder.comment.setShowingLine(5);
            holder.comment.setShowMoreColor(context.getResources().getColor(R.color.appColor));
            holder.comment.setShowLessTextColor(context.getResources().getColor(R.color.red));
            holder.comment.addShowMoreText("More");
            holder.comment.addShowLessText("Less");
        }


        try{
            Glide.with(context).load(commentModel.getUser_image()).placeholder(R.drawable.ic_chat).
                    error(R.drawable.ic_chat).into(holder.user_image);
        }catch (Exception e){
            holder.user_image.setImageResource(R.drawable.ic_chat);
        }


        /*****************************************************************/
    //    holder.txt_like.setVisibility(View.GONE);
   //     holder.image_like.setImageResource(R.drawable.like);
        //   for(int i= 0 ; i<post.getReactions().size();i++){
        if(commentModel.getReactions().containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            String reaction =  commentModel.getReactions().get(FirebaseAuth.getInstance().getCurrentUser().getUid());
            switch (reaction) {
                case "Like":
                    Log.e("FADY","Like"+ reaction);
                    holder.txt_like.setText("Like");
                    holder.txt_like.setVisibility(View.VISIBLE);
                    holder.txt_like.setTextColor(context.getResources().getColor(R.color.appColor));
               //     holder.image_like.setImageResource(R.drawable.ic_like);
                    break;
                case "Love":
                    Log.e("FADY","Love"+ reaction);
                    holder.txt_like.setText("Love");
                    holder.txt_like.setVisibility(View.VISIBLE);
                    holder.txt_like.setTextColor(context.getResources().getColor(R.color.red));
               //     holder.image_like.setImageResource(R.drawable.ic_love);
                    break;
                case "Haha":
                    Log.e("FADY","Haha"+ reaction);
                    holder.txt_like.setText("Haha");
                    holder.txt_like.setVisibility(View.VISIBLE);
                    holder.txt_like.setTextColor(context.getResources().getColor(R.color.yellow));
                 //   holder.image_like.setImageResource(R.drawable.ic_haha);
                    break;
                case "Sad":
                    holder.txt_like.setText("Sad");
                    holder.txt_like.setVisibility(View.VISIBLE);
                    holder.txt_like.setTextColor(context.getResources().getColor(R.color.yellow));
                 //   holder.image_like.setImageResource(R.drawable.ic_sad);
                    break;
                case "Wow":
                    holder.txt_like.setText("Wow");
                    holder.txt_like.setVisibility(View.VISIBLE);
                    holder.txt_like.setTextColor(context.getResources().getColor(R.color.yellow));
                 //   holder.image_like.setImageResource(R.drawable.ic_wow);
                    break;
                case "Angry":
                    Log.e("FADY","Angry"+ reaction);
                    holder.txt_like.setText("Angry");
                    holder.txt_like.setVisibility(View.VISIBLE);
                    holder.txt_like.setTextColor(context.getResources().getColor(R.color.yellow));
               //     holder.image_like.setImageResource(R.drawable.ic_angry);
                    break;

            }
            //       break;
        }else {
            Log.e("FADY","default");
         //   holder.txt_like.setVisibility(View.GONE);
        //    holder.image_like.setImageResource(R.drawable.like);
        }

    }

    @Override
    public int getItemCount() {
        return commentModels.size();
    }

    public class CommentsHolder extends RecyclerView.ViewHolder {
        TextView user_name,txt_comment,txt_like,name_only,numreaction,txt_date;
        ShowMoreTextView comment;
        CardView card_comment,card_image;
        ImageView user_image,image_comment;
        CircleImageView img_reaction;
        LinearLayout lin_reaction;

        public CommentsHolder(@NonNull View itemView) {
            super(itemView);
            comment = itemView.findViewById(R.id.comment);
            user_name = itemView.findViewById(R.id.user_name);
            user_image = itemView.findViewById(R.id.user_image);
            image_comment = itemView.findViewById(R.id.image_comment);
            txt_comment = itemView.findViewById(R.id.txt_comment);
            txt_like = itemView.findViewById(R.id.txt_like);
            card_comment=itemView.findViewById(R.id.card_comment);
            card_image=itemView.findViewById(R.id.card_image);
            name_only = itemView.findViewById(R.id.user_name_only);
            img_reaction = itemView.findViewById(R.id.img_reaction);
            numreaction=itemView.findViewById(R.id.numreaction);
            txt_date=itemView.findViewById(R.id.txt_date);
            lin_reaction=itemView.findViewById(R.id.lin_reaction);

            if(type.equals("comment")){
                txt_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(CheckNetwork.getConnectivityStatusString(context) == 1) {
                            onCommentClickListener.onClickComment(commentModels.get(getAdapterPosition()));
                        }else{
                            Toast.makeText(context, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                txt_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(CheckNetwork.getConnectivityStatusString(context) == 1) {
                            getReactionsDialog(commentModels.get(getAdapterPosition()));
                        }else{
                            Toast.makeText(context, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                lin_reaction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(CheckNetwork.getConnectivityStatusString(context) == 1) {
                            onCommentClickListener.onClickReaction(commentModels.get(getAdapterPosition()));
                        }else{
                            Toast.makeText(context, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }else{
                img_reaction.setVisibility(View.GONE);
                numreaction.setVisibility(View.GONE);
                txt_comment.setVisibility(View.GONE);
                txt_like.setVisibility(View.GONE);
            }

            image_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("getComment_image: ",commentModels.get(getAdapterPosition()).getComment_image());
                    onCommentClickListener.onClickIamge(commentModels.get(getAdapterPosition()).getComment_image());
                }
            });


        }
    }

    private DialogFragment getReactionsDialog(CommentModel commentModel){
        FBReactionDialog fbReactionDialog=new FBReactionDialog(new ReactionsListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onReactionsSelected(int reactionType) {
                switch (reactionType) {
                    case 0:
                        Log.e("reaction0", "Like");
                        reactionType2 = "Like";
                        onCommentClickListener.selectedReaction(reactionType2,commentModel);
                        break;
                    case 1:
                        Log.e("reaction", "Love");
                        reactionType2 = "Love";
                        onCommentClickListener.selectedReaction(reactionType2,commentModel);
                        break;
                    case 2:
                        reactionType2 = "Haha";
                        onCommentClickListener.selectedReaction(reactionType2,commentModel);
                        break;
                    case 3:
                        reactionType2 = "Sad";
                        onCommentClickListener.selectedReaction(reactionType2,commentModel);
                        break;
                    case 4:
                        reactionType2 = "Wow";
                        onCommentClickListener.selectedReaction(reactionType2,commentModel);
                        break;
                    case 5:
                        reactionType2 ="Angry";
                        onCommentClickListener.selectedReaction(reactionType2,commentModel);
                        break;

                }
            }
        });
        fbReactionDialog.show(fragmentManager,fbReactionDialog.getClass().getSimpleName());
        return fbReactionDialog;
    }

}
