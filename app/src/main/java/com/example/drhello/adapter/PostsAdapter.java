package com.example.drhello.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.drhello.connectionnewtwork.CheckNetwork;
import com.example.drhello.ui.writepost.TimeAgo;
import com.example.drhello.ui.writepost.FBReactionDialog;
import com.example.drhello.model.Posts;
import com.example.drhello.R;
import com.google.firebase.auth.FirebaseAuth;
import com.skyhope.showmoretextview.ShowMoreTextView;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsHolder>  {
    private Context context;
    private ArrayList<Posts> posts=new ArrayList<>();
    private OnPostClickListener onPostClickListener;
    private PostsHolder postsHolder;
    private  FragmentManager supportFragmentManager;
    private String reactionType2 = "0";
    private String name_activity ;

    public PostsAdapter() {

    }

    public PostsAdapter(Context context, ArrayList<Posts> posts,
                        OnPostClickListener onPostClickListener,
                        FragmentManager supportFragmentManager,String name_activity) {
        this.context = context;
        this.posts = posts;
        this.onPostClickListener = onPostClickListener;
        this.supportFragmentManager = supportFragmentManager;
        this.name_activity = name_activity;
        Log.e("PostsAdapter : ",posts.size()+"");
    }

    public void FunPostsAdapter( ArrayList<Posts> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public PostsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.posts_layout,parent,false);
        return new PostsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsHolder holder, int position) {
        Posts post =posts.get(position);
        postsHolder=holder;

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.US);
        TimeAgo timeAgo = new TimeAgo();

        try {
            Date date = dateFormat.parse(post.getDate());
            Log.e("getTime : ",date.getTime()+"");
            holder.date_post.setText(timeAgo.getTimeAgo(date.getTime(),true));
        } catch (ParseException e) {
            Log.e("getTime : ",e.getMessage());
            e.printStackTrace();
        }

        holder.like_react_use.setVisibility(View.GONE);
        holder.love_react_use.setVisibility(View.GONE);
        holder.haha_react_use.setVisibility(View.GONE);
        holder.sad_react_use.setVisibility(View.GONE);
        holder.wow_react_use.setVisibility(View.GONE);
        holder.angry_react_use.setVisibility(View.GONE);
        holder.img_reaction.setVisibility(View.GONE);
        holder.numreaction.setVisibility(View.VISIBLE);
        if (post.getReactions().size()!=0){
            holder.numreaction.setText(post.getReactions().size()+"");

            Collection<String> values = post.getReactions().values();
            ArrayList<String> list = new ArrayList(values);
            holder.img_reaction.setVisibility(View.VISIBLE);

            for (int i = 0; i < list.size(); i++) {
                Log.e("REACT: ", list.get(i));
                switch (list.get(i)) {
                    case "Like":
                        holder.like_react_use.setVisibility(View.VISIBLE);
                        break;
                    case "Love":
                        holder.love_react_use.setVisibility(View.VISIBLE);
                        break;
                    case "Haha":
                        holder.haha_react_use.setVisibility(View.VISIBLE);
                        break;
                    case "Sad":
                        holder.sad_react_use.setVisibility(View.VISIBLE);
                        break;
                    case "Wow":
                        holder.wow_react_use.setVisibility(View.VISIBLE);
                        break;
                    case "Angry":
                        holder.angry_react_use.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }else {
            holder.numreaction.setVisibility(View.GONE);
        }


        holder.user_name.setText(post.getNameUser());
        holder.commentnum.setText(post.getCommentNum()+"");

        try{
            Glide.with(context).load(post.getImageUser()).placeholder(R.drawable.user).
                    error(R.drawable.user).into(holder.user_image);
        }catch (Exception e){
            holder.user_image.setImageResource(R.drawable.user);
        }


        holder.recyclerView.setVisibility(View.GONE);
        //To read Images in post
        if (post.getImgUri().size()!=0){
            Log.e("POSTSIZE:",post.getImgUri().size()+""+post.getWritePost());

            holder.recyclerView.setVisibility(View.VISIBLE);
            ImageViewAdapter imageViewAdapter=new ImageViewAdapter(context,post.getImgUri(),post.getPostId(), onPostClickListener);
            GridLayoutManager recycleLayoutManager = new GridLayoutManager(context, 2,GridLayoutManager.VERTICAL, false);
            recycleLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    // grid items to take 1 column
                    if (post.getImgUri().size() % 2 == 0) {
                        return 1;
                    } else {
                        return (position == post.getImgUri().size()-1) ? 2 : 1;
                    }
                }
            });
            holder.recyclerView.setLayoutManager(recycleLayoutManager);
            holder.recyclerView.getLayoutManager().scrollToPosition(imageViewAdapter.getItemCount() - 1);
            holder.recyclerView.setAdapter(imageViewAdapter);
        }

        holder.txt_post.setText(post.getWritePost());



        if(post.getWritePost().length() > 500){
            holder.txt_post.addShowMoreText("More");
            holder.txt_post.addShowLessText("Less");
            holder.txt_post.setShowMoreColor(context.getResources().getColor(R.color.appColor));
            holder.txt_post.setShowLessTextColor(context.getResources().getColor(R.color.red));
            holder.txt_post.setShowingChar(500);
            holder.txt_post.setShowingLine(6);
        }else{
            holder.txt_post.addShowMoreText("");
            holder.txt_post.addShowLessText("");
        }

        holder.text_like.setVisibility(View.GONE);
        holder.image_like.setImageResource(R.drawable.like);
        if(post.getReactions().containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())){
           String reaction =  post.getReactions().get(FirebaseAuth.getInstance().getCurrentUser().getUid());
           switch (reaction) {
                case "Like":
                    Log.e("FADY","Like"+ reaction);
                    holder.text_like.setText("Like");
                    holder.text_like.setVisibility(View.VISIBLE);
                    holder.text_like.setTextColor(context.getResources().getColor(R.color.appColor));
                    holder.image_like.setImageResource(R.drawable.ic_like);
                    break;
                case "Love":
                    Log.e("FADY","Love"+ reaction);
                    holder.text_like.setText("Love");
                    holder.text_like.setVisibility(View.VISIBLE);
                    holder.text_like.setTextColor(context.getResources().getColor(R.color.red));
                    holder.image_like.setImageResource(R.drawable.ic_love);
                    break;
                case "Haha":
                    Log.e("FADY","Haha"+ reaction);
                    holder.text_like.setText("Haha");
                    holder.text_like.setVisibility(View.VISIBLE);
                    holder.text_like.setTextColor(context.getResources().getColor(R.color.yellow));
                    holder.image_like.setImageResource(R.drawable.ic_haha);
                    break;
                case "Sad":
                    holder.text_like.setText("Sad");
                    holder.text_like.setVisibility(View.VISIBLE);
                    holder.text_like.setTextColor(context.getResources().getColor(R.color.yellow));
                    holder.image_like.setImageResource(R.drawable.ic_sad);
                    break;
                case "Wow":
                    holder.text_like.setText("Wow");
                    holder.text_like.setVisibility(View.VISIBLE);
                    holder.text_like.setTextColor(context.getResources().getColor(R.color.yellow));
                    holder.image_like.setImageResource(R.drawable.ic_wow);
                    break;
                case "Angry":
                    Log.e("FADY","Angry"+ reaction);
                    holder.text_like.setText("Angry");
                    holder.text_like.setVisibility(View.VISIBLE);
                    holder.text_like.setTextColor(context.getResources().getColor(R.color.yellow));
                    holder.image_like.setImageResource(R.drawable.ic_angry);
                    break;

            }
     //       break;
        }else {
            Log.e("FADY","default");
            holder.text_like.setVisibility(View.GONE);
            holder.image_like.setImageResource(R.drawable.like);
        }




    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostsHolder  extends RecyclerView.ViewHolder {
        TextView   date_post , user_name,text_like,numreaction,commentnum,txt_comment;
        CircleImageView user_image;
        ImageView image_comment,image_share,image_like,post_option;
        ShowMoreTextView txt_post;
        RecyclerView recyclerView;
        LinearLayout lay_like,img_reaction;
        ImageView like_react_use,love_react_use,haha_react_use,sad_react_use,wow_react_use,angry_react_use;

        public PostsHolder(@NonNull View itemView) {
            super(itemView);
            txt_post=itemView.findViewById(R.id.txt_post);
            text_like=itemView.findViewById(R.id.text_like);
            date_post=itemView.findViewById(R.id.post_date);
            user_name=itemView.findViewById(R.id.user_name);
            user_image=itemView.findViewById(R.id.user_image);
            image_comment=itemView.findViewById(R.id.image_comment);
            image_like=itemView.findViewById(R.id.image_like);
            image_share=itemView.findViewById(R.id.image_share);
            recyclerView=itemView.findViewById(R.id.recycle_images_adapter);
            numreaction=itemView.findViewById(R.id.numreaction);
            commentnum = itemView.findViewById(R.id.commentnum);
            img_reaction = itemView.findViewById(R.id.img_reaction);
            txt_comment = itemView.findViewById(R.id.txt_comment);
            lay_like=itemView.findViewById(R.id.lay_like);
            like_react_use=itemView.findViewById(R.id.like_react_use);
            love_react_use=itemView.findViewById(R.id.love_react_use);
            haha_react_use=itemView.findViewById(R.id.haha_react_use);
            sad_react_use=itemView.findViewById(R.id.sad_react_use);
            wow_react_use=itemView.findViewById(R.id.wow_react_use);
            angry_react_use=itemView.findViewById(R.id.angry_react_use);
            post_option = itemView.findViewById(R.id.post_option);


            commentnum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(CheckNetwork.getConnectivityStatusString(context) == 1) {
                        onPostClickListener.onClickComment(posts.get(getAdapterPosition()));
                    }else{
                        Toast.makeText(context, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            image_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(CheckNetwork.getConnectivityStatusString(context) == 1) {
                        onPostClickListener.onClickShare(posts.get(getAdapterPosition()));
                    }else{
                        Toast.makeText(context, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            txt_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(CheckNetwork.getConnectivityStatusString(context) == 1) {
                        onPostClickListener.onClickComment(posts.get(getAdapterPosition()));
                    }else{
                        Toast.makeText(context, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            img_reaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(CheckNetwork.getConnectivityStatusString(context) == 1) {
                        onPostClickListener.onClickNumReaction(posts.get(getAdapterPosition()));
                    }else{
                        Toast.makeText(context, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            numreaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(CheckNetwork.getConnectivityStatusString(context) == 1) {
                        onPostClickListener.onClickNumReaction(posts.get(getAdapterPosition()));
                    }else{
                        Toast.makeText(context, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            image_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(CheckNetwork.getConnectivityStatusString(context) == 1) {
                        onPostClickListener.onClickComment(posts.get(getAdapterPosition()));
                    }else{
                        Toast.makeText(context, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            lay_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(CheckNetwork.getConnectivityStatusString(context) == 1) {
                        getReactionsDialog(posts.get(getAdapterPosition()));
                    }else{
                        Toast.makeText(context, "Please, Check Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            user_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPostClickListener.onClickProfile(getAdapterPosition(),posts.get(getAdapterPosition()).getUserId());
                }
            });

            post_option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(context, post_option);

                    // Inflating popup menu from popup_menu.xml file
                    popupMenu.getMenuInflater().inflate(R.menu.menu_post, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            // Toast message on menu item clicked
                            if(posts.get(getAdapterPosition()).getUserId().equals(FirebaseAuth.getInstance()
                                    .getCurrentUser().getUid())) {
                                popupMenu.getMenu().findItem(R.id.btn_delete).setVisible(true);
                                popupMenu.getMenu().findItem(R.id.btn_modify).setVisible(true);
                            }else{
                                popupMenu.getMenu().findItem(R.id.btn_delete).setVisible(false);
                                popupMenu.getMenu().findItem(R.id.btn_modify).setVisible(false);
                            }

                            if(!name_activity.equals("PostFragment")){
                                popupMenu.getMenu().findItem(R.id.btn_save).setVisible(false);
                                popupMenu.getMenu().findItem(R.id.btn_modify).setVisible(false);
                                popupMenu.getMenu().findItem(R.id.btn_delete).setVisible(true);
                            }

                            onPostClickListener.onClickOption(getAdapterPosition(),posts.get(getAdapterPosition()),menuItem);

                            return true;
                        }
                    });
                    // Showing the popup menu
                    popupMenu.show();
                }
            });

        }
    }



    private DialogFragment getReactionsDialog(Posts posts){
        FBReactionDialog fbReactionDialog=new FBReactionDialog(new ReactionsListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onReactionsSelected(int reactionType) {
                switch (reactionType) {
                    case 0:
                        Log.e("reaction0", "Like");
                        reactionType2 = "Like";
                        onPostClickListener.selectedReaction(reactionType2,posts);
                        break;
                    case 1:
                        Log.e("reaction", "Love");
                        reactionType2 = "Love";
                        onPostClickListener.selectedReaction(reactionType2,posts);
                        break;
                    case 2:
                        reactionType2 = "Haha";
                        onPostClickListener.selectedReaction(reactionType2,posts);
                        break;
                    case 3:
                        reactionType2 = "Sad";
                        onPostClickListener.selectedReaction(reactionType2,posts);
                        break;
                    case 4:
                        reactionType2 = "Wow";
                        onPostClickListener.selectedReaction(reactionType2,posts);
                        break;
                    case 5:
                        reactionType2 ="Angry";
                        onPostClickListener.selectedReaction(reactionType2,posts);
                        break;

                }
            }
        });
        fbReactionDialog.show(supportFragmentManager,fbReactionDialog.getClass().getSimpleName());
        return fbReactionDialog;
    }


}
