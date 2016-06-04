package com.example.rahulkumar.myfirebase;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rahulkumar on 30/05/16.
 */
public class FriendsRequestsModel implements Parcelable {


    public static final Creator<FriendsRequestsModel> CREATOR = new Creator<FriendsRequestsModel>() {
        @Override
        public FriendsRequestsModel createFromParcel(Parcel in) {
            return new FriendsRequestsModel(in);
        }

        @Override
        public FriendsRequestsModel[] newArray(int size) {
            return new FriendsRequestsModel[size];
        }
    };
    private String recipientid;
    private String status;
    private String ownerid;
    private String firstName;
    private String lastName;
    private String key;
    private String rfirstName;
    private String rlastName;
    private String createdAt;
    private String email;
    private String remail;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemail() {
        return remail;
    }

    public void setRemail(String remail) {
        this.remail = remail;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getRcreatedTime() {
        return RcreatedTime;
    }

    public void setRcreatedTime(String rcreatedTime) {
        RcreatedTime = rcreatedTime;
    }

    private String RcreatedTime;

    public FriendsRequestsModel() {

    }

    public FriendsRequestsModel(Parcel parcel) {
        recipientid = parcel.readString();
        status = parcel.readString();
        ownerid = parcel.readString();
        firstName = parcel.readString();
        lastName = parcel.readString();
        key = parcel.readString();
        rfirstName = parcel.readString();
        rlastName = parcel.readString();
    }

    public String getRecipientid() {
        return recipientid;
    }

    public void setRecipientid(String recipientid) {
        this.recipientid = recipientid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(String ownerid) {
        this.ownerid = ownerid;
    }

    public String getRlastName() {
        return rlastName;
    }

    public void setRlastName(String rlastName) {
        this.rlastName = rlastName;
    }

    public String getRfirstName() {
        return rfirstName;
    }

    public void setRfirstName(String rfirstName) {
        this.rfirstName = rfirstName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(recipientid);
        dest.writeString(status);
        dest.writeString(ownerid);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(key);
        dest.writeString(rfirstName);
        dest.writeString(rlastName);
    }

    /*create chat endpoint for firebase*/
    public String getChatRef(){
        return createUniqueChatRef();
    }



    private String createUniqueChatRef(){
        String uniqueChatRef="";
        if(createdAtCurrentUser()>createdAtRecipient()){
            uniqueChatRef=cleanEmailAddress(getEmail())+"-"+cleanEmailAddress(getRemail());
        }else {

            uniqueChatRef=cleanEmailAddress(getRemail())+"-"+cleanEmailAddress(getEmail());
        }
        return uniqueChatRef;
    }

    private long createdAtCurrentUser(){
        return Long.parseLong(getCreatedAt());
    }

    private long createdAtRecipient(){
        return Long.parseLong(getRcreatedTime());
    }

    private String cleanEmailAddress(String email){

        //replace dot with comma since firebase does not allow dot
        return email.replace(".","-");

    }
}
