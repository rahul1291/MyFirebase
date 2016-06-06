package com.example.rahulkumar.myfirebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity {

    private String mRecipientUid,mCurrentUid;
    SharedPreferences pref;

    @Bind(R.id.txtTitle)
    AppCompatTextView txtTitle;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.chat_recycler_view)
    RecyclerView chat_recycler_view;

    @Bind(R.id.chat_user_message)
    EditText chat_user_message;

    @Bind(R.id.sendUserMessage)
    Button sendUserMessage;

    @Bind(R.id.progress_for_chat)
    ProgressBar progress_for_chat;

    private MessageChatAdapter mMessageChatAdapter;
    /* Sender and Recipient status*/
    private static final int SENDER_STATUS=0;
    private static final int RECIPIENT_STATUS=1;

    /* unique Firebase ref for this chat */
    private Firebase mFirebaseMessagesChat;

    /* Listen to change in chat in firabase-remember to remove it */
    private ChildEventListener mMessageChatListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        pref=getSharedPreferences("MyApp",0);
        mCurrentUid=pref.getString("id","");
        Intent userdata=getIntent();
        FriendsRequestsModel friendsRequestsModel=userdata.getParcelableExtra(ReferenceUrl.KEY_PASS_USERS_INFO);
        System.out.println("value"+friendsRequestsModel.getOwnerid());
        if(friendsRequestsModel.getOwnerid().equalsIgnoreCase(mCurrentUid)){
            mRecipientUid=friendsRequestsModel.getRecipientid();
        }
        if(friendsRequestsModel.getRecipientid().equalsIgnoreCase(mCurrentUid)){
            mRecipientUid=friendsRequestsModel.getOwnerid();
        }




        chat_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        chat_recycler_view.setHasFixedSize(true);

        // Initialize adapter
        List<MessageChatModel> emptyMessageChat=new ArrayList<MessageChatModel>();
        mMessageChatAdapter=new MessageChatAdapter(emptyMessageChat);

        // Set adapter to recyclerView
        chat_recycler_view.setAdapter(mMessageChatAdapter);

        // Initialize firebase for this chat
        mFirebaseMessagesChat=new Firebase(ReferenceUrl.FIREBASE_CHAT_URL).child(ReferenceUrl.CHILD_CHAT).child(friendsRequestsModel.getChatRef());



    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.e(TAG, " I am onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();

        mMessageChatListener=mFirebaseMessagesChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {

                System.out.println("count"+dataSnapshot.getChildrenCount());
                if(dataSnapshot.exists()){
                    // Log.e(TAG, "A new chat was inserted");

                    MessageChatModel newMessage=dataSnapshot.getValue(MessageChatModel.class);
                    if(newMessage.getSender().equals(mCurrentUid)){
                        newMessage.setRecipientOrSenderStatus(SENDER_STATUS);
                    }else{
                        newMessage.setRecipientOrSenderStatus(RECIPIENT_STATUS);
                    }
                    mMessageChatAdapter.refillAdapter(newMessage);
                    chat_recycler_view.scrollToPosition(mMessageChatAdapter.getItemCount()-1);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

        // Remove listener
        if(mMessageChatListener !=null) {
            // Remove listener
            mFirebaseMessagesChat.removeEventListener(mMessageChatListener);
        }
        // Clean chat message
        mMessageChatAdapter.cleanUp();

    }


    public void sendMessageToFireChat(View sendButton){
        String senderMessage=chat_user_message.getText().toString();
        senderMessage=senderMessage.trim();

        if(!senderMessage.isEmpty()){

            // Log.e(TAG, "send message");

            // Send message to firebase
            Map<String, String> newMessage = new HashMap<String, String>();
            newMessage.put("sender", mCurrentUid); // Sender uid
            newMessage.put("recipient",mRecipientUid); // Recipient uid
            newMessage.put("message",senderMessage); // Message

            mFirebaseMessagesChat.push().setValue(newMessage);

            // Clear text
            chat_user_message.setText("");

        }
    }


}
