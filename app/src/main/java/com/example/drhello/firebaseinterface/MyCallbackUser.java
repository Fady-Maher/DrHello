package com.example.drhello.firebaseinterface;
import com.google.firebase.firestore.DocumentSnapshot;

public interface MyCallbackUser {
    void onCallback(DocumentSnapshot documentSnapshot);
}
