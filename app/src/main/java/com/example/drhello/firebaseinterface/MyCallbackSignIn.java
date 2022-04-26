package com.example.drhello.firebaseinterface;

import com.example.drhello.model.UserAccount;
import com.google.firebase.firestore.DocumentSnapshot;

public interface MyCallbackSignIn {
    void onCallback(DocumentSnapshot documentSnapshot);
}
