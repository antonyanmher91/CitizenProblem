package com.example.problem.model;

public class CommentsModel {
    private String comments;
    private String usernaem;
    private String userimg;

    public CommentsModel(String comments, String usernaem, String userimg) {
        this.comments = comments;
        this.usernaem = usernaem;
        this.userimg = userimg;
    }

    public String getComments() {
        return comments;
    }

    public String getUsernaem() {
        return usernaem;
    }

    public String getUserimg() {
        return userimg;
    }
}
