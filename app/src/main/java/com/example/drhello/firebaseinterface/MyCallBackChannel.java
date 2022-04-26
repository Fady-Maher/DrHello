package com.example.drhello.firebaseinterface;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public interface MyCallBackChannel {
    void onCallback(DocumentSnapshot documentSnapshot);
}
