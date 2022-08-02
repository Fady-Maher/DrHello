package com.example.drhello.ui.chats;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import com.example.drhello.R;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AsyncTaskDownloadAudio extends AsyncTask<String, String, String> {
    Context context;
    ProgressDialog dialog;
    boolean downloading_status = false;
    private static double SPACE_KB = 1024;
    private static double SPACE_MB = 1024 * SPACE_KB;
    private static double SPACE_GB = 1024 * SPACE_MB;
    private static double SPACE_TB = 1024 * SPACE_GB;
    long total = 0;
    int lenghtOfFile = 0;


    public AsyncTaskDownloadAudio(Context context){
        this.context = context;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        settingProgressDialog();
        Toast.makeText(context, "start Downloading", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String doInBackground(String... f_url) {
        int count;

        try {
            URL url = new URL(f_url[0]);
            URLConnection connection = url.openConnection();
            connection.connect();

            lenghtOfFile = connection.getContentLength();
            // download the file
            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);
            // Output stream
            OutputStream output = new FileOutputStream(getFilename());
            byte data[] = new byte[1024];
            total = 0;
            while ((count = input.read(data)) != -1) {
                if (downloading_status || !this.isCancelled()) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    // writing data to file
                    output.write(data, 0, count);
                } else {
                    break;
                }
            }
            // flushing output
            output.flush();
            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }

    protected void onProgressUpdate(String... progress) {
        downloading_status = true;
        Log.e("progess : ", progress[0] + "");
        dialog.setProgressNumberFormat((bytes2String(total)) + "/" + (bytes2String(lenghtOfFile)));
        dialog.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String file_url) {
        Toast.makeText(context, "finish download", Toast.LENGTH_SHORT).show();
        downloading_status = false;
        dialog.dismiss();
    }


    private void settingProgressDialog() {
        total = 0;
        lenghtOfFile = 0;
        dialog = new ProgressDialog(context, R.style.AlertDialogCustom);
        dialog.setMessage("Download in progress ...");
        dialog.setTitle("Downloading Audio");
        dialog.setMax(100);
        dialog.setIcon(R.drawable.ic_save);
        dialog.setProgressNumberFormat((bytes2String(total)) + "/" + (bytes2String(lenghtOfFile)));
        dialog.setProgress(0);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AsyncTaskDownloadAudio.this.cancel(true);
                downloading_status = false;  //add boolean check
                File fdelete = new File(getFilename());
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        Toast.makeText(context, "Cancel Downloading ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed Cancel Downloading ", Toast.LENGTH_SHORT).show();
                    }
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public static String bytes2String(long sizeInBytes) {

        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits(2);

        try {
            if (sizeInBytes < SPACE_KB) {
                return nf.format(sizeInBytes) + " Byte(s)";
            } else if (sizeInBytes < SPACE_MB) {
                return nf.format(sizeInBytes / SPACE_KB) + " KB";
            } else if (sizeInBytes < SPACE_GB) {
                return nf.format(sizeInBytes / SPACE_MB) + " MB";
            } else if (sizeInBytes < SPACE_TB) {
                return nf.format(sizeInBytes / SPACE_GB) + " GB";
            } else {
                return nf.format(sizeInBytes / SPACE_TB) + " TB";
            }
        } catch (Exception e) {
            return sizeInBytes + " Byte(s)";
        }

    }


    public String getFilename() {

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                , "DOWNLOAD_AUDIO");
        Log.e("file : ", file.getAbsolutePath());
        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp3");
    }


}