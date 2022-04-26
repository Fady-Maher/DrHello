package com.example.drhello.firebaseinterface;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public interface MyCallBackMessage {
    void onCallback(Task<QuerySnapshot> task);
}
