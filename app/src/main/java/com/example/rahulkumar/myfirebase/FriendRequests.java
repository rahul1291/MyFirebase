package com.example.rahulkumar.myfirebase;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FriendRequests extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.rv_friends)
    RecyclerView rv_friends;

    @Bind(R.id.txtNoFriendsRequests)
    AppCompatTextView txtNoFriendsRequests;

    Firebase firebase, firebasefriends;

    SharedPreferences prefs;
    String id;

    List<FriendsRequestsModel> friendsRequestsModelList = new ArrayList<>();

    FriendRequestsListAdapter friendRequestsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        firebase = MySingleton.getInstanceUsingDoubleLocking();
        firebasefriends = firebase.child(ReferenceUrl.CHILD_FRIENDS);
        prefs = getSharedPreferences("MyApp", 0);
        id = prefs.getString("id", "");

        friendRequestsListAdapter = new FriendRequestsListAdapter(friendsRequestsModelList);
        rv_friends.setLayoutManager(new LinearLayoutManager(this));
        rv_friends.setHasFixedSize(true);
        rv_friends.setAdapter(friendRequestsListAdapter);
        FetchRequest();
    }

    public void FetchRequest() {
        MyProgressDialog.ShowDialog(FriendRequests.this);
        firebasefriends.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MyProgressDialog.Dismiss();
                if (dataSnapshot.exists()) {
                    FriendsRequestsModel model = dataSnapshot.getValue(FriendsRequestsModel.class);
                    String ownerid = model.getOwnerid();
                    if (!id.equalsIgnoreCase(ownerid) && id.equalsIgnoreCase(model.getRecipientid()) && model.getStatus().equalsIgnoreCase("pending")) {
                        model.setKey(dataSnapshot.getKey());
                        friendsRequestsModelList.add(model);
                    }
                    if (friendsRequestsModelList.size() > 0) {
                        rv_friends.setVisibility(View.VISIBLE);
                        txtNoFriendsRequests.setVisibility(View.GONE);
                        friendRequestsListAdapter.Notify(friendsRequestsModelList);
                    } else {
                        rv_friends.setVisibility(View.GONE);
                        txtNoFriendsRequests.setVisibility(View.VISIBLE);
                    }
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


}
