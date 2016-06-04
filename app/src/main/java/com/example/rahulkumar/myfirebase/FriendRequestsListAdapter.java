package com.example.rahulkumar.myfirebase;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by rahulkumar on 30/05/16.
 */
public class FriendRequestsListAdapter extends RecyclerView.Adapter<FriendRequestsListAdapter.ViewHolder> {

    List<FriendsRequestsModel> friendsRequestsModels;
    Context context;
    int position;

    public FriendRequestsListAdapter(List<FriendsRequestsModel> friendsRequestsModels) {
        this.friendsRequestsModels = friendsRequestsModels;
    }

    @Override
    public FriendRequestsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friendrequests_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendRequestsListAdapter.ViewHolder holder, int position) {

        holder.txtName.setText(friendsRequestsModels.get(position).getFirstName() + "  " + friendsRequestsModels.get(position).getLastName());

    }

    public void Notify(List<FriendsRequestsModel> friendsRequestsModels) {
        this.friendsRequestsModels = friendsRequestsModels;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return friendsRequestsModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        @Bind(R.id.iv_image)
        CircleImageView iv_image;

        @Bind(R.id.txtName)
        AppCompatTextView txtName;

        @Bind(R.id.btn_accept)
        AppCompatButton btn_accept;

        @Bind(R.id.btn_reject)
        AppCompatButton btn_reject;

        Firebase firebase;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            firebase = MySingleton.getInstanceUsingDoubleLocking();
        }

        @OnClick(R.id.btn_accept)
        public void Accept() {

            DecisionMaking("accepted");
        }

        @OnClick(R.id.btn_reject)
        public void Reject() {
            DecisionMaking("rejected");
        }

        public void DecisionMaking(final String choice) {
            position = getAdapterPosition();
            String key = friendsRequestsModels.get(position).getKey();
            firebase = firebase.child(ReferenceUrl.CHILD_FRIENDS).child(key);
            Map<String, Object> map = new HashMap<>();
            map.put("status", choice);
            MyProgressDialog.ShowDialog(context);
            firebase.updateChildren(map, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    MyProgressDialog.Dismiss();
                    if (firebaseError == null) {
                        Toast.makeText(context, choice + " " + "Successfully", Toast.LENGTH_SHORT).show();
                        friendsRequestsModels.remove(position);
                        FriendRequestsListAdapter.this.notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
