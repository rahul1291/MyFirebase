package com.example.rahulkumar.myfirebase;

import android.content.Context;
import android.content.SharedPreferences;
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
 * Created by rahulkumar on 22/05/16.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {


    static List<UsersChatModel> usersChatModels;
    static int position;
    static Context context;
    String firstnamee;
    String lastnamee;

    public SearchAdapter(List<UsersChatModel> usersChatModels) {
        this.usersChatModels = usersChatModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtName.setText(usersChatModels.get(position).getFirstName() + "  " + usersChatModels.get(position).getLastName());
    }

    public void SetFirstLastName(String firstname, String lastname) {
        firstnamee = firstname;
        lastnamee = lastname;
    }

    @Override
    public int getItemCount() {
        return usersChatModels.size();
    }

    public void Notify(List<UsersChatModel> usersChatModels) {
        this.usersChatModels = usersChatModels;
        System.out.println("sizee is" + usersChatModels.size());
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_image)
        CircleImageView iv_image;

        @Bind(R.id.txtName)
        AppCompatTextView txtName;

        @Bind(R.id.btn_addfriend)
        AppCompatButton btn_addfriend;

        Firebase firebaseRootRef;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.btn_addfriend)
        public void AddFriend() {
            position = getAdapterPosition();
            firebaseRootRef = MySingleton.getInstanceUsingDoubleLocking();
            String id = usersChatModels.get(position).getRecipientUid();
            SharedPreferences prefs = context.getSharedPreferences("MyApp", 0);
            String uid = prefs.getString("id", "");
            System.out.println("id is" + id);
            Map<String, Object> map = new HashMap<>();
            map.put(ReferenceUrl.OWNER_ID, uid);
            map.put(ReferenceUrl.RECIPIENT_ID, id);
            map.put(ReferenceUrl.STATUS, "pending");
            map.put(ReferenceUrl.KEY_FIRST_NAME, firstnamee);
            map.put(ReferenceUrl.KEY_LAST_NAME, lastnamee);
            map.put(ReferenceUrl.RECIPIENT_FIRST_NAME, usersChatModels.get(position).getFirstName());
            map.put(ReferenceUrl.RECIPIENT_LAST_NAME, usersChatModels.get(position).getLastName());
            MyProgressDialog.ShowDialog(context);
            firebaseRootRef.child(ReferenceUrl.CHILD_FRIENDS).push().setValue(map, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    MyProgressDialog.Dismiss();
                    if (firebaseError == null) {
                        usersChatModels.remove(position);
                        SearchAdapter.super.notifyDataSetChanged();
                        Toast.makeText(context, "Successfully added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
