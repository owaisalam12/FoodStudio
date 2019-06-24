package com.core2plus.oalam.foodstudio.Entity;

public class UserData {
    private String UserName;
    private String UserEmail;
    private String UserMobile;
    private String UserPass;
    private String UserImage;

//    public UserData(String userName, String userEmail, String userMobile, String userImage) {
//        UserName = userName;
//        UserEmail = userEmail;
//        UserMobile = userMobile;
//        UserImage=userImage;
//    }

    public UserData(String userName, String userEmail, String userMobile) {
        UserName = userName;
        UserEmail = userEmail;
        UserMobile = userMobile;
    }
    public UserData(String userName, String userEmail,String userPass, String userMobile) {
        UserName = userName;
        UserEmail = userEmail;
        UserMobile = userMobile;
        UserPass=userPass;
    }

    public String getUserPass() {
        return UserPass;
    }

    public void setUserPass(String userPass) {
        UserPass = userPass;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }

    public UserData() {
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getUserMobile() {
        return UserMobile;
    }

    public void setUserMobile(String userMobile) {
        UserMobile = userMobile;
    }
}
