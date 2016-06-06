package com.example.rahulkumar.myfirebase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by rahulkumar on 21/05/16.
 */
public class MyFriendsAdapter extends RecyclerView.Adapter<MyFriendsAdapter.ViewHolder> {


    static List<FriendsRequestsModel> friendsRequestsModelList;
    SharedPreferences prefs;
    String id;

    static Context context;

    public MyFriendsAdapter(List<FriendsRequestsModel> friendsRequestsModelList) {
        this.friendsRequestsModelList = friendsRequestsModelList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        prefs = parent.getContext().getSharedPreferences("MyApp", 0);
        id = prefs.getString("id", "");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friends_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (id.equalsIgnoreCase(friendsRequestsModelList.get(position).getOwnerid())) {
            holder.txtName.setText(friendsRequestsModelList.get(position).getRfirstName() + " " + friendsRequestsModelList.get(position).getRlastName());
        }

        if (id.equalsIgnoreCase(friendsRequestsModelList.get(position).getRecipientid())) {
            holder.txtName.setText(friendsRequestsModelList.get(position).getFirstName() + " " + friendsRequestsModelList.get(position).getLastName());
        }

    }

    public void Notify(List<FriendsRequestsModel> friendsRequestsModelList) {
        this.friendsRequestsModelList = friendsRequestsModelList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return friendsRequestsModelList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.iv_image)
        CircleImageView imageView;

        @Bind(R.id.txtName)
        AppCompatTextView txtName;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition(); // Get row position

            FriendsRequestsModel user = friendsRequestsModelList.get(position); // Get use object
            System.out.println("created at"+user.getCreatedAt());
            System.out.println("rcreated at"+user.getRcreatedTime());
            // Create a chat activity
            Intent chatIntent = new Intent(context, ChatActivity.class);
            // Attach data to activity as a parcelable object
            chatIntent.putExtra(ReferenceUrl.KEY_PASS_USERS_INFO, user);
            context.startActivity(chatIntent);
        }
    }
}
