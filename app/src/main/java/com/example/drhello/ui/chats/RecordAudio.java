package com.example.drhello.ui.chats;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.IOException;

public class RecordAudio {

    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    public static MediaRecorder recorder = null;
    private int currentFormat = 0;
    private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4,MediaRecorder.OutputFormat.THREE_GPP };
    private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP };
    private static final String PHOTOS_FOLDER = "Photos";



    /** Called when the activity is first created. */

    public String getFilename(){

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                ,AUDIO_RECORDER_FOLDER);
        Log.e("file : ", file.getAbsolutePath());
        if(!file.exists()){
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + "a" + ".mp3");
    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Log.i("AudioRecorder","Error: " + what + ", " + extra);
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Log.e("AudioRecorder","Warning: " + what + ", " + extra);
        }
    };

    public void startRecording(){
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(output_formats[currentFormat]);
        //recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(getFilename());
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);

        try {
            recorder.prepare();
            recorder.start();
            Log.e("prepare : ","start");
        } catch (IllegalStateException e) {
            Log.e("prepareERR:", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("IOException : ", e.getMessage());

            e.printStackTrace();
        }
    }



    public void stopRecording(){
        if(recorder != null){
            try {
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
            }catch (Exception e){
               Log.e("AudioRecorder",e.getMessage());

            }

        //
           // ChatActivity.uploadAudio(Uri.fromFile(new File(getFilename())));
        }
    }
}
