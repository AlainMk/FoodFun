package com.alain.mk.mygestion.api;

import com.alain.mk.mygestion.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    public static CollectionReference getUserCollection(){

        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);

    }

    // --- CREATE --- //

    public static Task<Void> createUser(String uid, String username, String urlPicture){

        User userToCreate = new User(uid, username, urlPicture);
        return UserHelper.getUserCollection().document(uid).set(userToCreate);
    }

    // --- GET --- //

    public static Task<DocumentSnapshot> getUser(String uid){

        return UserHelper.getUserCollection().document(uid).get();
    }
}
