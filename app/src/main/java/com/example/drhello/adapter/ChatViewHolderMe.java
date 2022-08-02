package com.example.drhello.adapter;


import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drhello.R;

public class ChatViewHolderMe extends RecyclerView.ViewHolder {

    private TextView txt_date, txt_message,txt_timestamp , txt_speed;
    private ImageView imageView;
    private ImageButton btn_start_pause , btn_share_message ,btn_download_record_me;
    private TextView txt_time_end, txt_time_start;
    private SeekBar seekBarDuration;
    private ConstraintLayout constraint,all_contraint;

    public ChatViewHolderMe(@NonNull View itemView) {
        super(itemView);

        txt_message = itemView.findViewById(R.id.txt_message_me);
        txt_date = itemView.findViewById(R.id.txt_date_me);
        txt_timestamp = itemView.findViewById(R.id.txt_timestamp_me);
        imageView = itemView.findViewById(R.id.image_chat_me);

        //player media
        btn_start_pause = itemView.findViewById(R.id.btn_start_pause_me);
        txt_time_end = itemView.findViewById(R.id.txt_times_end_me);
        txt_time_start = itemView.findViewById(R.id.txt_time_start_me);
        seekBarDuration = itemView.findViewById(R.id.seekBar_me);
        constraint = itemView.findViewById(R.id.constraint_me);
        txt_speed = itemView.findViewById(R.id.txt_speed_me);
        btn_download_record_me = itemView.findViewById(R.id.btn_download_record_me);
        all_contraint = itemView.findViewById(R.id.all_contraint);
    }

    public ImageButton getBtn_download_record_me() {
        return btn_download_record_me;
    }

    public void setBtn_download_record_me(ImageButton btn_download_record_me) {
        this.btn_download_record_me = btn_download_record_me;
    }

    public ImageButton getBtn_share_message() {
        return btn_share_message;
    }

    public void setBtn_share_message(ImageButton btn_share_message) {
        this.btn_share_message = btn_share_message;
    }

    public TextView getTxt_speed() {
        return txt_speed;
    }

    public void setTxt_speed(TextView txt_speed) {
        this.txt_speed = txt_speed;
    }

    public TextView getTxt_date() {
        return txt_date;
    }

    public void setTxt_date(TextView txt_date) {
        this.txt_date = txt_date;
    }

    public TextView getTxt_message() {
        return txt_message;
    }

    public void setTxt_message(TextView txt_message) {
        this.txt_message = txt_message;
    }

    public TextView getTxt_timestamp() {
        return txt_timestamp;
    }

    public void setTxt_timestamp(TextView txt_timestamp) {
        this.txt_timestamp = txt_timestamp;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public ImageButton getBtn_start_pause() {
        return btn_start_pause;
    }

    public void setBtn_start_pause(ImageButton btn_start_pause) {
        this.btn_start_pause = btn_start_pause;
    }

    public TextView getTxt_time_end() {
        return txt_time_end;
    }

    public void setTxt_time_end(TextView txt_time_end) {
        this.txt_time_end = txt_time_end;
    }

    public TextView getTxt_time_start() {
        return txt_time_start;
    }

    public void setTxt_time_start(TextView txt_time_start) {
        this.txt_time_start = txt_time_start;
    }

    public SeekBar getSeekBarDuration() {
        return seekBarDuration;
    }

    public void setSeekBarDuration(SeekBar seekBarDuration) {
        this.seekBarDuration = seekBarDuration;
    }

    public ConstraintLayout getConstraint() {
        return constraint;
    }

    public void setConstraint(ConstraintLayout constraint) {
        this.constraint = constraint;
    }

    public ConstraintLayout getAll_contraint() {
        return all_contraint;
    }

    public void setAll_contraint(ConstraintLayout all_contraint) {
        this.all_contraint = all_contraint;
    }
}