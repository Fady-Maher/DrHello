package com.example.drhello.firebaseinterface;

import com.google.firebase.firestore.DocumentSnapshot;

public interface MyCallbackSignIn {
    void onCallback(DocumentSnapshot documentSnapshot);
}
