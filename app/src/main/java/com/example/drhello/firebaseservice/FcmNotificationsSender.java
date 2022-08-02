package com.example.drhello.firebaseservice;

import android.app.Activity;
import android.content.Context;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class FcmNotificationsSender {

    String userFcmToken;
    String title;
    String body;
    Context mContext;
    Activity mActivity;
    String image, postId = "", idUser;

    private RequestQueue requestQueue;
    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey =
            "AAAAi-LC8XE:APA91bFIKsKSlvn4zzLMrjEzanHR0F8MCLRzFIhdT_jYfaJrQwGrIsF_z82mels6iGNyCE5nXU7M8zWR7bPltbquEt1WAP5ezz85T4jju5jiaq3U8_MkWC7c4wDrOk3WNCxgHDBJimng";

    public FcmNotificationsSender(String userFcmToken,
                                  String uid,
                                  String post,
                                  String body,
                                  Context applicationContext,
                                  Activity mActivity,
                                  String imageUser) {
        this.userFcmToken = userFcmToken;
        this.title = post;
        this.body = body;
        this.mContext = applicationContext;
        this.mActivity = mActivity;
        this.image = imageUser;
        this.idUser = uid;
    }

    public FcmNotificationsSender(String userFcmToken, String uid,
                                  String comment, String body,
                                  Context applicationContext,
                                  Activity mActivity,
                                  String user_image,
                                  String postId) {
        this.userFcmToken = userFcmToken;
        this.title = comment;
        this.body = body;
        this.mContext = applicationContext;
        this.mActivity = mActivity;
        this.image = user_image;
        this.idUser = uid;
        this.postId = postId;
    }


    public void SendNotifications() {

        requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", userFcmToken);
            JSONObject notiObject = new JSONObject();
            notiObject.put("title", title);
            notiObject.put("body", body);
            notiObject.put("icon", image); // enter icon that exists in drawable only
            notiObject.put("image", image);
            notiObject.put("color", postId);
            notiObject.put("idUser", idUser);
            mainObj.put("data", notiObject);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, response -> {
                // code run is got response

            }, error -> {

            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + fcmServerKey);
                    return header;
                }
            };
            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}