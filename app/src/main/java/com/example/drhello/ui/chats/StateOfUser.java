package com.example.drhello.ui.chats;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.drhello.model.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class StateOfUser {
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    private FirebaseFirestore db= FirebaseFirestore.getInstance();

    public void changeState(String state){
        if(mAuth.getCurrentUser() != null){
            db.collection("users").document(mAuth.getCurrentUser().getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        Log.e("task : " , " tast");
                        UserAccount userAccount = task.getResult().toObject(UserAccount.class);
                        if(userAccount != null){
                            userAccount.setState(state);
                            db.collection("users").document(mAuth.getCurrentUser().getUid())
                                    .set(userAccount);
                        }
                    }
                }
            });
        }
    }
}
