package com.example.rahulkumar.myfirebase;

import com.firebase.client.Firebase;

/**
 * Created by rahulkumar on 20/05/16.
 */
public class MySingleton {
    private static Firebase firebase = null;

    private MySingleton() {

    }

    public static Firebase getInstanceUsingDoubleLocking() {
        if (firebase == null) {
            synchronized (MySingleton.class) {
                if (firebase == null) {
                    firebase = new Firebase(ReferenceUrl.FIREBASE_CHAT_URL);
                }
            }
        }
        return firebase;
    }
}
