package com.example.drhello.adapter;

import com.example.drhello.model.ChatModel;

public interface OnClickMessageListener {
    void onLongClickImage(ChatModel chatModel,int position,boolean flagLongClick,int action);
    void onLongClickAudio(ChatModel chatModel,int position,boolean flagLongClick,int action);
    void onLongClickText(ChatModel chatModel,int position ,boolean flagLongClick,int action);

}
