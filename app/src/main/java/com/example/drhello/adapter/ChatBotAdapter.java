package com.example.drhello.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.drhello.R;
import com.example.drhello.model.ChatBotModel;
import java.util.ArrayList;
import pl.droidsonroids.gif.GifImageButton;


public class ChatBotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<ChatBotModel> arrayList = new ArrayList<>();
    final private int viewholdermeID = 0, viewholderotherID = 1;
    private OnTranslateClickListener onTranslateClickListener;
    private ChatBotlistener chatBotlistener;
    public ChatBotAdapter(Context context, ArrayList<ChatBotModel> arrayList,OnTranslateClickListener onTranslateClickListener,ChatBotlistener chatBotlistener) {
        this.context = context;
        this.arrayList = arrayList;
        this.onTranslateClickListener = onTranslateClickListener;
        this.chatBotlistener = chatBotlistener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == viewholdermeID) {
            view = LayoutInflater.from(context).inflate(R.layout.chattingbotme, parent, false);
            return new ChatBotViewHolderme(view);
        }
        view = LayoutInflater.from(context).inflate(R.layout.chattingbotspeek, parent, false);
        return new ChatBotViewHolderBot(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatBotModel chatBotModel = arrayList.get(position);
        String time = splitDateTime(chatBotModel.getDate())[1].substring(0, splitDateTime(chatBotModel.getDate())[1].length() - 6) + " ";
        String timestamp = time + splitDateTime(chatBotModel.getDate())[2];
        switch (holder.getItemViewType()) {
            case viewholdermeID:
                ChatBotViewHolderme chatBotViewHolderme  = (ChatBotViewHolderme) holder;
                chatBotViewHolderme.txt_date.setText(splitDateTime(chatBotModel.getDate())[0]);
                chatBotViewHolderme.txt_message.setText(chatBotModel.getText());
                chatBotViewHolderme.txt_timestamp.setText(timestamp);
                break;
            case viewholderotherID:
                ChatBotViewHolderBot chatBotViewHolderBot  = (ChatBotViewHolderBot) holder;
                chatBotViewHolderBot.txt_date.setText(splitDateTime(chatBotModel.getDate())[0]);
                chatBotViewHolderBot.txt_message.setText(chatBotModel.getText());
                chatBotViewHolderBot.txt_timestamp.setText(timestamp);
                if(chatBotModel.getText().contains("http")){
                    Log.e("RED: ","http");
                    chatBotViewHolderBot.txt_message.setTextColor(context.getResources().getColor(R.color.appColorUnSelected));
                    String link = "http"+chatBotModel.getText().split("http")[1];
                    SpannableString content = new SpannableString(link);
                    content.setSpan(new UnderlineSpan(), 0, link.length(), 0);
                    chatBotViewHolderBot.txt_message.setText(content);
                }else{
                    chatBotViewHolderBot.txt_message.setTextColor(context.getResources().getColor(R.color.white));
                }
                break;
            default:
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayList.get(position).getType() == 0) {
            return viewholdermeID;
        } else {
            return viewholderotherID;
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private String[] splitDateTime(String dateFormat) {
        return dateFormat.split(" ");
    }

    public class ChatBotViewHolderBot extends RecyclerView.ViewHolder {
        private TextView txt_timestamp, txt_message, txt_date;
        private GifImageButton gifImageButton;
        public ChatBotViewHolderBot(@NonNull View itemView) {
            super(itemView);
            txt_date = itemView.findViewById(R.id.txt_date_other);
            txt_message = itemView.findViewById(R.id.txt_message_other);
            txt_timestamp = itemView.findViewById(R.id.txt_timestamp_other);
            gifImageButton = itemView.findViewById(R.id.btn_share_message_other);

            gifImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTranslateClickListener.onClick(arrayList.get(getAdapterPosition()),getAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (arrayList.get(getAdapterPosition()).getText().contains("http"))
                        chatBotlistener.onClick("http"+arrayList.get(getAdapterPosition()).getText().split("http")[1]);
                }
            });
        }
    }

    public class ChatBotViewHolderme extends RecyclerView.ViewHolder {
        private TextView txt_timestamp, txt_message, txt_date;
        public ChatBotViewHolderme(@NonNull View itemView) {
            super(itemView);
            txt_date = itemView.findViewById(R.id.txt_date_me);
            txt_message = itemView.findViewById(R.id.txt_message_me);
            txt_timestamp = itemView.findViewById(R.id.txt_timestamp_me);

        }
    }
}
