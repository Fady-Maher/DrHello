package com.example.drhello.viewmodel;

import android.app.ProgressDialog;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.drhello.model.CommentModel;
import com.example.drhello.model.Posts;
import com.example.drhello.ui.writecomment.InsideCommentActivity;
import com.example.drhello.ui.writecomment.WriteCommentActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.UUID;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;

public class CommentViewModel extends ViewModel {
    private static final String TAG = "PostsViewModel";

    public MutableLiveData<ArrayList<CommentModel>> commentsMutableLiveData = new MutableLiveData<>();
    public ArrayList<CommentModel> commentList = new ArrayList<>();
    public StorageReference storageReference;

    public void uploadComment(FirebaseFirestore db,
                              byte[] bytes, Posts posts, CommentModel commentModel, CommentModel commentModel2, ProgressDialog mProgress) {
        storageReference = FirebaseStorage.getInstance().getReference(posts.getUserId());
        Completable completable1 = Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                    db.collection("posts").document(posts.getPostId()).
                            collection("comments").add(commentModel)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        String id = task.getResult().getId();
                                        commentModel.setComment_id(id);
                                        Log.e("uploadComment : ", commentModel.getComment_id());
                                        db.collection("posts").document(posts.getPostId()).
                                                collection("comments").document(id).set(commentModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    if (bytes != null) {
                                                        Log.e("POP : ", "");
                                                        uploadImage(bytes, posts, commentModel, storageReference, db, commentModel2,mProgress);
                                                    } else {
                                                        WriteCommentActivity.mProgress.dismiss();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            });
            }
        });
        completable1.subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: Uploading");
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public void uploadCommentInside(FirebaseFirestore db,
                              byte[] bytes, Posts posts, CommentModel commentModel, CommentModel commentModel2,ProgressDialog mProgress) {
        storageReference = FirebaseStorage.getInstance().getReference(posts.getUserId());
        Completable completable1 = Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                    db.collection("posts").document(posts.getPostId()).
                            collection("comments").document(commentModel.getComment_id())
                            .collection("InsideComments").add(commentModel2)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        String id = task.getResult().getId();
                                        commentModel2.setComment_id(id);
                                        Log.e("uploadComment : ", commentModel2.getComment_id());
                                        db.collection("posts").document(posts.getPostId()).
                                                collection("comments").document(commentModel.getComment_id())
                                                .collection("InsideComments").document(id)
                                                .set(commentModel2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    if (bytes != null) {
                                                        Log.e("POP : ", "");
                                                        uploadImage(bytes, posts, commentModel, storageReference, db, commentModel2, mProgress);
                                                    } else {
                                                        Log.e("mProgress : ", "mProgress");
                                                        InsideCommentActivity.mProgress.dismiss();
                                                    }

                                                }
                                            }
                                        });
                                    }
                                }
                            });
            }
        });
        completable1.subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: Uploading");
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }



    private void uploadImage(byte[] bytes, Posts posts, CommentModel commentModel
            , StorageReference storageReference, FirebaseFirestore db,
                             CommentModel commentModel2,  ProgressDialog mProgress) {
        if (commentModel2 == null) {
            Log.e("getComment_id : ", commentModel.getComment_id());
            StorageReference ref = storageReference.child(posts.getPostId() + "/images/comment/"
                    + commentModel.getComment_id() + "/" + UUID.nameUUIDFromBytes(bytes));
            ref.putBytes(bytes)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                saveUri(posts, ref, db, commentModel, commentModel2,mProgress);
                            } else {
                                //Toast.makeText(WritePostsActivity.this, "Loading not done", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Toast.makeText(WritePostsActivity.this, "Image not loading error : "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("getComment_id : ", commentModel2.getComment_id());
            StorageReference ref = storageReference.child(posts.getPostId() + "/images/comment/"
                    + commentModel.getComment_id() + "/"
                    + commentModel2.getComment_id() + "/" + UUID.nameUUIDFromBytes(bytes));
            ref.putBytes(bytes)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                saveUri(posts, ref, db, commentModel, commentModel2, mProgress);
                            } else {
                                //Toast.makeText(WritePostsActivity.this, "Loading not done", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Toast.makeText(WritePostsActivity.this, "Image not loading error : "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveUri(Posts posts, StorageReference ref, FirebaseFirestore db, CommentModel commentModel, CommentModel commentModel2, ProgressDialog mProgress) {

        if (commentModel2 == null) {
            ref.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            commentModel.setComment_image(uri.toString());
                            db.collection("posts").document(posts.getPostId()).
                                    collection("comments").document(commentModel.getComment_id()).set(commentModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                        mProgress.dismiss();
                                }
                            });
                           // WriteCommentActivity.mProgress.dismiss();
                            Log.e("saveUri : ", commentModel.getComment_id());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("errorH : ", e.getMessage());
                }
            });
        } else {
            ref.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            commentModel2.setComment_image(uri.toString());
                            db.collection("posts").document(posts.getPostId()).
                                    collection("comments").document(commentModel.getComment_id())
                                    .collection("InsideComments").document(commentModel2.getComment_id())
                                    .set(commentModel2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.e("task onComp: ", task.isSuccessful()+"");
                                    InsideCommentActivity.mProgress.dismiss();
                                }
                            });
                            Log.e("saveUri : ", commentModel.getComment_id());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("errorH : ", e.getMessage());
                }
            });
        }
    }
    


}
