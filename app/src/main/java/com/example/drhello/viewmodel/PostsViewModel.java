package com.example.drhello.viewmodel;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.drhello.model.PostsRejects;
import com.example.drhello.model.Posts;
import com.example.drhello.model.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.UUID;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class PostsViewModel extends ViewModel {
    private static final String TAG = "PostsViewModel";
    public MutableLiveData<Posts> postsMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<List<Posts>> postsOffMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Integer>  isfinishgetposts = new MutableLiveData<>();
    public MutableLiveData<Integer>  isfinish = new MutableLiveData<>();
    PostsRejects postsRejects;
    public int i =0;
    public void uploadImages(String collection, FirebaseFirestore db , StorageReference storageReference , List<byte[]> bytes ,
                             List<String> uriImage , Posts posts, UserAccount userAccount){
        i =0;
        Completable completable1 = Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                if(collection.equals("postsRejects")){
                    postsRejects = new PostsRejects(posts.getWritePost(),posts.getUserId(),posts.getPostId(),posts.getImgUri(),
                            posts.getDate(),posts.getNameUser(),posts.getImageUser(),posts.getTokneId(),
                            posts.getReactionNumber(),posts.getCommentNum(),
                            posts.getReactions());
                    postsRejects.setEmail(userAccount.getEmail());
                    db.collection(collection).add(postsRejects).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()){
                                String id = task.getResult().getId();
                                postsRejects.setPostId(id);
                                posts.setPostId(id);
                                Log.e("posts run :" , postsRejects.getPostId());
                                db.collection(collection).document(id).set(postsRejects);
                                //Toast.makeText(WritePostsActivity.this, "الحمدلله يارب", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onComplete: Post Success");
                                if(bytes.size() == 0){
                                    isfinish.setValue(i);
                                    Log.e("isfinish : ", " " + isfinish);
                                }
                            }else {

                            }
                        }
                    });
                }else{
                    db.collection(collection).add(posts).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()){
                                String id = task.getResult().getId();
                                posts.setPostId(id);
                                Log.e("posts run :" , posts.getPostId());
                                db.collection(collection).document(id).set(posts);
                                //Toast.makeText(WritePostsActivity.this, "الحمدلله يارب", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onComplete: Post Success");
                                if(bytes.size() == 0){
                                    isfinish.setValue(i);
                                    Log.e("isfinish : ", " " + isfinish);
                                }
                            }else {

                            }
                        }
                    });
                }
            }
        });

        Completable completable2 = Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                Log.e("Exception : ", " " + isfinish);

                Thread.sleep(1000);
                if(bytes.size() > 0){
                    uploadImage(collection,bytes,uriImage,posts,storageReference,db,postsRejects);
                    Log.e("isfinish : ", " " + isfinish);

                }else{
                //    WritePostsActivity.mProgress.dismiss();
                    isfinish.setValue(1000);
                    Log.e("isfinish : ", " " + isfinish);
                }

            }
        });
        completable1.mergeWith(completable2)
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
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
    private void uploadImage(String collection,List<byte[]> bytes,List<String> uriImage , Posts posts,
                             StorageReference storageReference,FirebaseFirestore db,PostsRejects postsRejects) {
        //isfinish.setValue(0);
        for (byte[] bytesOutImg : bytes) {
            Log.e("uploadImage ", posts.getPostId());
            StorageReference ref = storageReference.child(posts.getPostId() + "/images/" + UUID.nameUUIDFromBytes(bytesOutImg));
            ref.putBytes(bytesOutImg)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        saveUri(collection,uriImage, posts, db,uri.toString(),postsRejects);
                                        Log.d(TAG, "onComplete: Img Uploaded");
                                    }
                                });


                                Log.e("isfinish ", isfinish.toString()+"");
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
        if (i == bytes.size()-1){
            Log.d(TAG, "uploadImage: " + i);
            Log.e("error ", i+"");
        }

    }
    private void saveUri(String collection,List<String> uriImage,Posts posts,FirebaseFirestore db,String uri,PostsRejects postsRejects) {
        //UUID imageName = UUID.nameUUIDFromBytes(bytesOutImg);
        if(collection.equals("postsRejects")){
            Log.e("saveUri ", postsRejects.getPostId());
            uriImage.add(uri);
            postsRejects.setImgUri(uriImage);
            db.collection(collection).document(postsRejects.getPostId()).set(postsRejects).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        //Toast.makeText(WritePostsActivity.this, "الحمدلله يارب", Toast.LENGTH_SHORT).show();
                        i++;
                        isfinish.setValue(i);
                        Log.d(TAG, "onComplete: save uri ");

                    }else {
                        //Toast.makeText(WritePostsActivity.this, "error ", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }else{
            Log.e("saveUri ", posts.getPostId());
            uriImage.add(uri);
            posts.setImgUri(uriImage);
            db.collection(collection).document(posts.getPostId()).set(posts).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        //Toast.makeText(WritePostsActivity.this, "الحمدلله يارب", Toast.LENGTH_SHORT).show();
                        i++;
                        isfinish.setValue(i);
                        Log.d(TAG, "onComplete: save uri ");

                    }else {
                        //Toast.makeText(WritePostsActivity.this, "error ", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }

}

