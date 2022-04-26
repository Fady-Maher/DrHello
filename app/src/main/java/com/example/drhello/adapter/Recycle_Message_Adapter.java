package com.example.drhello.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.drhello.model.LastMessages;
import com.example.drhello.ui.chats.AsyncTaskDownloadAudio;
import com.example.drhello.ui.chats.MediaPlayerCustom;
import com.example.drhello.R;
import com.example.drhello.model.ChatModel;
import com.example.drhello.ui.main.MainActivity;
import com.example.drhello.ui.writepost.ShowImageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class Recycle_Message_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<ChatModel> list_message ;
    Context context;
    Bitmap bitmap;
    private final FirebaseUser user;
    final private int viewholdermeID = 0, viewholderotherID = 1;

    private int lastPosition = 0;

    private final MediaPlayerCustom player;

    public Recycle_Message_Adapter(ArrayList<ChatModel> list_message, Context context, Bitmap bitmap) {
        this.list_message = list_message;
        this.context = context;
        this.bitmap = bitmap;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        player = new MediaPlayerCustom();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list_message.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        if (viewType == viewholdermeID) {
            view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_chat_me, parent, false);
            return new ChatViewHolderMe(view);
        }
        view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_chat_other, parent, false);
        return new ChatViewHolderOther(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatModel message = list_message.get(position);
        String time = splitDateTime(message.getDate())[1].substring(0, splitDateTime(message.getDate())[1].length() - 6) + " ";
        String timestamp = time + splitDateTime(message.getDate())[2];

        switch (holder.getItemViewType()) {
            case viewholdermeID:
                ChatViewHolderMe chatViewHolderMe = (ChatViewHolderMe) holder;
                if (message.getMessage().equals("") && message.getRecord().equals("")) { // image recieve
                    chatViewHolderMe.getTxt_date().setText(splitDateTime(message.getDate())[0]);
                    chatViewHolderMe.getTxt_timestamp().setText(timestamp);
                    try{
                        Glide.with(context).load(message.getImage()).placeholder(R.drawable.ic_chat)
                                .error(R.drawable.ic_chat).into(chatViewHolderMe.getImageView());
                    }catch (Exception e){
                        chatViewHolderMe.getImageView().setImageResource(R.drawable.ic_chat);
                    }


                    chatViewHolderMe.getImageView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, ShowImageActivity.class);
                            byte[] byteArray;
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byteArray = stream.toByteArray();
                            intent.putExtra("image_profile",byteArray);
                            stream = new ByteArrayOutputStream();
                            getBitmapFromImage(chatViewHolderMe).compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byteArray = stream.toByteArray();

                            intent.putExtra("image_show",byteArray);
                            context.startActivity(intent);
                        }
                    });

                    chatViewHolderMe.getTxt_message().setVisibility(View.GONE);
                    chatViewHolderMe.getConstraint().setVisibility(View.GONE);
                    chatViewHolderMe.getImageView().setVisibility(View.VISIBLE);

                } else if (message.getImage().equals("") && message.getRecord().equals("")) { // message recieve

                    chatViewHolderMe.getTxt_date().setText(splitDateTime(message.getDate())[0]);
                    chatViewHolderMe.getTxt_message().setText(message.getMessage());
                    chatViewHolderMe.getTxt_message().setMovementMethod(LinkMovementMethod.getInstance());
                    chatViewHolderMe.getTxt_message().setHighlightColor(Color.TRANSPARENT);
                    chatViewHolderMe.getTxt_timestamp().setText(timestamp);
                    chatViewHolderMe.getImageView().setVisibility(View.GONE);
                    chatViewHolderMe.getConstraint().setVisibility(View.GONE);
                    chatViewHolderMe.getTxt_message().setVisibility(View.VISIBLE);
                    if(message.getMessage().contains("http")){
                        chatViewHolderMe.getTxt_message().setLinkTextColor(Color.WHITE);
                    }

                } else {
                    //record recieve

                    chatViewHolderMe.getTxt_date().setText(splitDateTime(message.getDate())[0]);
                    chatViewHolderMe.getTxt_timestamp().setText(timestamp);
                    chatViewHolderMe.getTxt_message().setVisibility(View.GONE);
                    chatViewHolderMe.getImageView().setVisibility(View.GONE);
                    chatViewHolderMe.getTxt_speed().setVisibility(View.GONE);
                    chatViewHolderMe.getTxt_time_start().setText("00:00");
                    chatViewHolderMe.getSeekBarDuration().setProgress(0);
                    chatViewHolderMe.getSeekBarDuration().setEnabled(false);
                    chatViewHolderMe.getBtn_start_pause().setBackgroundResource(R.drawable.ic_play);
                    chatViewHolderMe.getConstraint().setVisibility(View.VISIBLE);
                    chatViewHolderMe.getBtn_download_record_me().setVisibility(View.VISIBLE);


                    chatViewHolderMe.getSeekBarDuration().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (fromUser && player.getPlaying()) {
                                player.getPlayer().seekTo(seekBar.getProgress());
                                seekBar.setProgress(progress);
                            }
                        }
                    });

                    chatViewHolderMe.getBtn_start_pause().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (lastPosition != chatViewHolderMe.getAdapterPosition()) {
                                Log.e("pos click : ", chatViewHolderMe.getAdapterPosition() + "");
                                notifyItemChanged(lastPosition);
                                player.setFlag_start(true);
                                lastPosition = chatViewHolderMe.getAdapterPosition();

                                Log.e("notifyDataSetChanged:", " " + chatViewHolderMe.getAdapterPosition());

                                //  notifyDataSetChanged();
                                Log.e("lastPosition", "!= position  " + chatViewHolderMe.getAdapterPosition());
                            }

                            chatViewHolderMe.getTxt_speed().setVisibility(View.VISIBLE);
                            chatViewHolderMe.getBtn_download_record_me().setVisibility(View.GONE);
                            if (chatViewHolderMe.getAdapterPosition() != RecyclerView.NO_POSITION) {

                                if (player.isFlag_start()) {
                                    //  Log.e("pre= next->flag_start", "not change item"+"pre : "+pre+"   next"+ next);
                                    Log.e("pos original : ", chatViewHolderMe.getAdapterPosition() + "");
                                    player.stopPlaying();
                                    player.preparedMediaPlayer(true, chatViewHolderMe, message);
                                    player.setFlag_start(false);
                                } else {
                                    Log.e("isPlaying", player.getPlaying() + "");
                                    if (player.getPlaying()) {
                                        chatViewHolderMe.getBtn_start_pause().setBackgroundResource(R.drawable.ic_play);
                                        player.setPlaying(true);
                                    } else {
                                        chatViewHolderMe.getBtn_start_pause().setBackgroundResource(R.drawable.ic_pause);
                                        player.setPlaying(false);
                                    }
                                    player.pausePlaying(chatViewHolderMe, message);
                                }
                            }

                        }
                    });

                    chatViewHolderMe.getTxt_speed().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!player.isFlag_start()){
                                player.changeSpeedAudio(chatViewHolderMe);
                            }
                        }
                    });


                }


                chatViewHolderMe.getBtn_share_message().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("message",message);
                        context.startActivity(intent);
                    }
                });




                chatViewHolderMe.getBtn_download_record_me().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!message.getRecord().equals("")){
                            new AsyncTaskDownloadAudio(context).execute(message.getRecord());
                        }
                    }
                });



                break;

            case viewholderotherID:
                ChatViewHolderOther chatViewHolderOther = (ChatViewHolderOther) holder;
                if (message.getMessage().equals("") && message.getRecord().equals("")) { // image recieve


                    chatViewHolderOther.getTxt_date().setText(splitDateTime(message.getDate())[0]);
                    chatViewHolderOther.getTxt_timestamp().setText(timestamp);
                    chatViewHolderOther.getImage_profile().setImageBitmap(bitmap);
                    try{
                        Glide.with(context).load(message.getImage()).placeholder(R.drawable.ic_chat)
                                .error(R.drawable.ic_chat).into(chatViewHolderOther.getImageView());
                    }catch (Exception e){
                        chatViewHolderOther.getImageView().setImageResource(R.drawable.ic_chat);
                    }

                    chatViewHolderOther.getImageView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, ShowImageActivity.class);
                            byte[] byteArray;
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byteArray = stream.toByteArray();
                            intent.putExtra("image_profile",byteArray);
                            stream = new ByteArrayOutputStream();
                            getBitmapFromImage(chatViewHolderOther).compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byteArray = stream.toByteArray();

                            intent.putExtra("image_show",byteArray);

                            context.startActivity(intent);
                        }
                    });

                    chatViewHolderOther.getTxt_message().setVisibility(View.GONE);
                    chatViewHolderOther.getImageView().setVisibility(View.VISIBLE);
                    chatViewHolderOther.getConstraint().setVisibility(View.GONE);
                } else if (message.getImage().equals("") && message.getRecord().equals("")) { // message recieve

                    chatViewHolderOther.getTxt_date().setText(splitDateTime(message.getDate())[0]);
                    chatViewHolderOther.getTxt_message().setText(message.getMessage());
                    chatViewHolderOther.getTxt_message().setMovementMethod(LinkMovementMethod.getInstance());
                    chatViewHolderOther.getTxt_message().setHighlightColor(Color.TRANSPARENT);
                    chatViewHolderOther.getTxt_timestamp().setText(timestamp);
                    chatViewHolderOther.getImage_profile().setImageBitmap(bitmap);
                    chatViewHolderOther.getImageView().setVisibility(View.GONE);
                    chatViewHolderOther.getTxt_message().setVisibility(View.VISIBLE);
                    chatViewHolderOther.getConstraint().setVisibility(View.GONE);
                    if(message.getMessage().contains("http")){
                        chatViewHolderOther.getTxt_message().setLinkTextColor(Color.WHITE);
                    }
                } else {
                    //record recieve

                    chatViewHolderOther.getTxt_date().setText(splitDateTime(message.getDate())[0]);
                    chatViewHolderOther.getTxt_timestamp().setText(timestamp);
                    chatViewHolderOther.getImage_profile().setImageBitmap(bitmap);
                    chatViewHolderOther.getTxt_message().setVisibility(View.GONE);
                    chatViewHolderOther.getImageView().setVisibility(View.GONE);
                    chatViewHolderOther.getTxt_time_start().setText("00:00");
                    chatViewHolderOther.getTxt_speed().setVisibility(View.GONE);
                    chatViewHolderOther.getSeekBarDuration().setProgress(0);
                    chatViewHolderOther.getSeekBarDuration().setEnabled(false);
                    chatViewHolderOther.getBtn_start_pause().setBackgroundResource(R.drawable.ic_play);
                    chatViewHolderOther.getConstraint().setVisibility(View.VISIBLE);
                    chatViewHolderOther.getBtn_download_record_other().setVisibility(View.VISIBLE);
                    chatViewHolderOther.getSeekBarDuration().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (fromUser && player.getPlaying()) {
                                player.getPlayer().seekTo(seekBar.getProgress());
                                seekBar.setProgress(progress);
                            }
                        }
                    });


                    chatViewHolderOther.getBtn_start_pause().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (lastPosition != chatViewHolderOther.getAdapterPosition()) {
                                notifyItemChanged(lastPosition);
                                player.setFlag_start(true);
                                lastPosition = chatViewHolderOther.getAdapterPosition();
                                Log.e("lastPosition", "!= position");
                            }

                            chatViewHolderOther.getTxt_speed().setVisibility(View.VISIBLE);
                            chatViewHolderOther.getBtn_download_record_other().setVisibility(View.GONE);

                            if (chatViewHolderOther.getAdapterPosition() != RecyclerView.NO_POSITION) {
                                if (player.isFlag_start()) {
                                    //   Log.e("pre= next->flag_start", "not change item"+"pre : "+pre+"   next"+ next);
                                    player.stopPlaying();
                                    player.preparedMediaPlayer(true, chatViewHolderOther, message);
                                    player.setFlag_start(false);
                                } else {
                                    Log.e("isPlaying", player.getPlaying() + "");
                                    if (player.getPlaying()) {
                                        chatViewHolderOther.getBtn_start_pause().setBackgroundResource(R.drawable.ic_play);
                                        player.setPlaying(true);
                                    } else {
                                        chatViewHolderOther.getBtn_start_pause().setBackgroundResource(R.drawable.ic_pause);
                                        player.setPlaying(false);
                                    }
                                    player.pausePlaying(chatViewHolderOther, message);
                                }
                            }


                        }
                    });


                    chatViewHolderOther.getTxt_speed().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!player.isFlag_start()){
                                player.changeSpeedAudio(chatViewHolderOther);
                            }
                        }
                    });




                }


                chatViewHolderOther.getBtn_share_message().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("message",message);
                        context.startActivity(intent);
                    }
                });


                chatViewHolderOther.getBtn_download_record_other().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!message.getRecord().equals("")){
                            new AsyncTaskDownloadAudio(context).execute(message.getRecord());
                        }
                    }
                });


                break;
        }

    }

    public void updateMessage(ArrayList<ChatModel> list_message){
        this.list_message = list_message;
        notifyDataSetChanged();
    }

    public void addMessage(ChatModel chatModel){
      //  list_message.add(chatModel);
        if(!list_message.contains(chatModel)){

            Log.e("list_message: " , list_message.contains(chatModel)+"");

            list_message.add(0,chatModel);
        }

       // notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if (user.getUid().equals(list_message.get(position).getSenderid())) {
            return viewholdermeID;
        } else {
            return viewholderotherID;
        }
    }

    @Override
    public long getItemId(int position) {
        return getItemViewType(position);
    }


    private String[] splitDateTime(String dateFormat) {
        return dateFormat.split(" ");
    }

    public void stoppingPlayer() {
        player.stopPlaying();
    }

    private Bitmap getBitmapFromImage(ChatViewHolderMe chatViewHolderMe) {
        Bitmap bitmap;
        try {
            bitmap = ((BitmapDrawable) chatViewHolderMe.getImageView().getDrawable()).getBitmap();
        } catch (Exception E) {
            bitmap = Bitmap.createBitmap(chatViewHolderMe.getImageView().getDrawable().getIntrinsicWidth(), chatViewHolderMe.getImageView().getDrawable().getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            chatViewHolderMe.getImageView().getDrawable().setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            chatViewHolderMe.getImageView().getDrawable().draw(canvas);
        }
        return bitmap;
    }


    private Bitmap getBitmapFromImage(ChatViewHolderOther chatViewHolderOther) {
        Bitmap bitmap;
        try {
            bitmap = ((BitmapDrawable) chatViewHolderOther.getImageView().getDrawable()).getBitmap();
        } catch (Exception E) {
            bitmap = Bitmap.createBitmap(chatViewHolderOther.getImageView().getDrawable().getIntrinsicWidth(), chatViewHolderOther.getImageView().getDrawable().getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            chatViewHolderOther.getImageView().getDrawable().setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            chatViewHolderOther.getImageView().getDrawable().draw(canvas);
        }
        return bitmap;
    }
}