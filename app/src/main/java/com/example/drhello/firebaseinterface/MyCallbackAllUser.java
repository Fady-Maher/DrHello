package com.example.drhello.firebaseinterface;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public interface MyCallbackAllUser {
    void onCallback(Task<QuerySnapshot> task);
}
