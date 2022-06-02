package com.example.drhello.firebaseinterface;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.ListResult;

public interface MyCallbackDeleteItem {
    void myCallBackItem(Task<Void> task);
}
