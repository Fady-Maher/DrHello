package com.example.drhello.ui.chats;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import com.example.drhello.R;
import com.example.drhello.adapter.ChatViewHolderMe;
import com.example.drhello.adapter.ChatViewHolderOther;
import com.example.drhello.model.ChatModel;
import java.io.IOException;

public class MediaPlayerCustom {

    boolean isPlaying =false, flag_start = true ;
    private MediaPlayer player;
    int length = 0;
    float  speed_current = 0f ,speed_default = 1.0f, speed_medium = 1.5f, speed_double = 2.0f;

    public MediaPlayer getPlayer() {
        return player;
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }

    public boolean getPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isFlag_start() {
        return flag_start;
    }

    public void setFlag_start(boolean flag_start) {
        this.flag_start = flag_start;
    }

    public void preparedMediaPlayer(boolean flag, ChatViewHolderMe chatViewHolderMe, ChatModel message){

        if(flag){
            startPlaying(chatViewHolderMe,message.getRecord());
        }

        Log.e("preparedMediaPlayer", "preparedMediaPlayer");

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("run", isPlaying+"run");
                        while( player != null && isPlaying )
                        {


                            chatViewHolderMe.getSeekBarDuration().setProgress(player.getCurrentPosition());
                            Message msg=new Message();
                            try{
                                if(player != null && player.isPlaying() && isPlaying ){
                                    int millis = player.getCurrentPosition();
                                    msg.obj=millis/1000;
                                    handler.sendMessage(msg);
                                    handler.post(new Runnable(){
                                        public void run() {
                                            if(player != null)
                                                chatViewHolderMe.getTxt_time_start().setText(millisecondsToTime(player.getCurrentPosition()));
                                        }
                                    });
                                }

                        }catch (IllegalStateException E){
                            Log.e("error ", "force stopPlaying");
                            chatViewHolderMe.getBtn_download_record_me().setVisibility(View.VISIBLE);
                            stopPlaying();
                        }


                        try {
                                Thread.sleep(100);
                            }
                            catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        }, 1);



        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                chatViewHolderMe.getTxt_speed().setText("1x");
                chatViewHolderMe.getTxt_speed().setVisibility(View.GONE);
                chatViewHolderMe.getSeekBarDuration().setProgress(0);
                Log.e("seekbar : ",chatViewHolderMe.getSeekBarDuration().getProgress()+"");
                chatViewHolderMe.getTxt_time_start().setText("00:00");
                chatViewHolderMe.getSeekBarDuration().setEnabled(false);
                chatViewHolderMe.getBtn_start_pause().setBackgroundResource(R.drawable.ic_play);
                chatViewHolderMe.getBtn_download_record_me().setVisibility(View.VISIBLE);
                Log.e(":onCompletion()", "true1");
                stopPlaying();
                flag_start = true;
                isPlaying = false;
            }
        });
    }




    public void startPlaying(ChatViewHolderMe chatViewHolderMe, String url) {
        Log.e("pos start : ",chatViewHolderMe.getAdapterPosition()+"");
        player = new MediaPlayer();
        Log.e("startPlaying", "startPlaying");
        try {
            player.reset();
            player.setDataSource(url);
            player.prepare();
            player.setVolume(1.0f, 1.0f);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    mp.start();
                    Log.e("isPlaying", "true");
                    isPlaying = true;
                    chatViewHolderMe.getSeekBarDuration().setEnabled(true);
                    chatViewHolderMe.getBtn_start_pause().setBackgroundResource(R.drawable.ic_pause);
                    chatViewHolderMe.getTxt_time_end().setText(millisecondsToTime(player.getDuration()));
                    chatViewHolderMe.getSeekBarDuration().setProgress(0);
                    chatViewHolderMe.getSeekBarDuration().setMax(player.getDuration());
                    Log.d("Prog", "run: " + player.getDuration());
                }
            });
        } catch (IOException e) {
            Log.e(":playRecording()", "prepare() failed");
        }


    }



    public void pausePlaying(ChatViewHolderMe chatViewHolderMe , ChatModel message) {
        if(player != null){
            if(isPlaying && player.isPlaying()){
                player.pause();
                length=player.getCurrentPosition();
                isPlaying = false;
                Log.e(":pause  ", "true");
            }else{
                player.seekTo(length);
                player.start();
                isPlaying = true;
                Log.e("resume  ", "true");
                preparedMediaPlayer(false,chatViewHolderMe ,message);
            }
        }
    }







    public void preparedMediaPlayer(boolean flag, ChatViewHolderOther chatViewHolderOther, ChatModel message){

        if(flag){
            startPlaying(chatViewHolderOther,message.getRecord());
        }

        Log.e("preparedMediaPlayer", "preparedMediaPlayer");

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("run", isPlaying+"run");
                        while( player != null && isPlaying )
                        {
                            chatViewHolderOther.getSeekBarDuration().setProgress(player.getCurrentPosition());
                            Message msg=new Message();
                            try {
                                if(player != null && player.isPlaying() && isPlaying ){
                                    int millis = player.getCurrentPosition();
                                    msg.obj=millis/1000;
                                    handler.sendMessage(msg);
                                    handler.post(new Runnable(){
                                        public void run() {
                                            if(player != null)
                                                chatViewHolderOther.getTxt_time_start().setText(millisecondsToTime(player.getCurrentPosition()));
                                        }
                                    });
                                }
                            }catch (IllegalStateException E){
                                Log.e("error ", "force stopPlaying");
                                chatViewHolderOther.getBtn_download_record_other().setVisibility(View.VISIBLE);
                                stopPlaying();
                            }


                            try {
                                Thread.sleep(100);
                            }
                            catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        }, 100);



        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                chatViewHolderOther.getTxt_speed().setText("1x");
                chatViewHolderOther.getTxt_speed().setVisibility(View.GONE);
                chatViewHolderOther.getSeekBarDuration().setProgress(0);
                Log.e("seekbar : ",chatViewHolderOther.getSeekBarDuration().getProgress()+"");
                chatViewHolderOther.getTxt_time_start().setText("00:00");
                chatViewHolderOther.getSeekBarDuration().setEnabled(false);
                chatViewHolderOther.getBtn_start_pause().setBackgroundResource(R.drawable.ic_play);
                chatViewHolderOther.getBtn_download_record_other().setVisibility(View.VISIBLE);
                Log.e(":onCompletion()", "true1");
                stopPlaying();
                flag_start = true;
                isPlaying = false;
            }
        });
    }



    public void startPlaying(ChatViewHolderOther chatViewHolderOther, String url) {

        player = new MediaPlayer();
        Log.e("startPlaying", "startPlaying");
        try {
            player.reset();
            player.setDataSource(url);
            player.prepare();
            player.setVolume(1.0f, 1.0f);

            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    mp.start();
                    Log.e("isPlaying", "true");
                    isPlaying = true;
                    chatViewHolderOther.getSeekBarDuration().setEnabled(true);
                    chatViewHolderOther.getBtn_start_pause().setBackgroundResource(R.drawable.ic_pause);
                    chatViewHolderOther.getTxt_time_end().setText(millisecondsToTime(player.getDuration()));
                    chatViewHolderOther.getSeekBarDuration().setProgress(0);
                    chatViewHolderOther.getSeekBarDuration().setMax(player.getDuration());
                    Log.d("Prog", "run: " + player.getDuration());
                }
            });
        } catch (IOException e) {
            Log.e(":playRecording()", "prepare() failed");
        }
    }



    public void pausePlaying(ChatViewHolderOther chatViewHolderOther , ChatModel message) {
        if(player != null){
            if(isPlaying && player.isPlaying()){
                player.pause();
                length=player.getCurrentPosition();
                isPlaying = false;
                Log.e(":pause  ", "true");
            }else{
                player.seekTo(length);
                player.start();
                isPlaying = true;
                Log.e("resume  ", "true");
                preparedMediaPlayer(false,chatViewHolderOther ,message);
            }
        }
    }

    public void stopPlaying() {
        if(player != null){
            try {
                player.stop();
                player.reset();
                player.release();
                isPlaying = false;
                player = null;
                Log.e("player  ", "= null ");
            }catch (Exception e){
                Log.e("Exception", e.toString());
            }
        }
    }

    private float getSpeed(){
        return speed_current;
    }
    private void setSpeed(float speed){
        this.speed_current = speed;
    }

    public void changeSpeedAudio(ChatViewHolderOther chatViewHolderOther){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           if(speed_current == speed_default){
               chatViewHolderOther.getTxt_speed().setText("1.5x");
               player.setPlaybackParams(player.getPlaybackParams().setSpeed(speed_medium));
               speed_current = speed_medium;
           }else if(speed_current == speed_medium){
               chatViewHolderOther.getTxt_speed().setText("2x");
               player.setPlaybackParams(player.getPlaybackParams().setSpeed(speed_double));
               speed_current = speed_double;
           }else{
               chatViewHolderOther.getTxt_speed().setText("1x");
               player.setPlaybackParams(player.getPlaybackParams().setSpeed(speed_default));
               speed_current = speed_default;
           }
        }
    }

    public void changeSpeedAudio(ChatViewHolderMe chatViewHolderMe){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(speed_current == speed_default){
                chatViewHolderMe.getTxt_speed().setText("1.5x");
                player.setPlaybackParams(player.getPlaybackParams().setSpeed(speed_medium));
                speed_current = speed_medium;
            }else if(speed_current == speed_medium){
                chatViewHolderMe.getTxt_speed().setText("2x");
                player.setPlaybackParams(player.getPlaybackParams().setSpeed(speed_double));
                speed_current = speed_double;
            }else{
                chatViewHolderMe.getTxt_speed().setText("1x");
                player.setPlaybackParams(player.getPlaybackParams().setSpeed(speed_default));
                speed_current = speed_default;
            }
        }
    }

    private String millisecondsToTime(long milliseconds) {
        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;
        String secondsStr = Long.toString(seconds);
        String secs;
        if (secondsStr.length() >= 2) {
            secs = secondsStr.substring(0, 2);
        } else {
            secs = "0" + secondsStr;
        }

        return minutes + ":" + secs;
    }


}
