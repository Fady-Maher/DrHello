package com.example.drhello.firebaseinterface;

import com.google.firebase.firestore.DocumentSnapshot;

public interface MyCallBackChannel {
    void onCallback(DocumentSnapshot documentSnapshot);
}
