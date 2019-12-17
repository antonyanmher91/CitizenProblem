package com.example.problem.model;

public class CommentsModel {
    private String comments;
    private String userName;
    private String userImg;

    public CommentsModel(String comments, String usernaem, String userimg) {
        this.comments = comments;
        this.userName = usernaem;
        this.userImg = userimg;
    }

    public CommentsModel() {
    }
    
    public String getComments() {
        return comments;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserImg() {
        return userImg;
    }
}
