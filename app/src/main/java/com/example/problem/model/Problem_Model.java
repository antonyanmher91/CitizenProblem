package com.example.problem.model;

import android.net.Uri;

import java.io.Serializable;

public class Problem_Model implements Serializable {
    private String userimg = "";

    public String getProblemsType() {
        return problemsType;
    }

    private String problemsType;
    private String problemimg = "";
    private String name;
    private int like;
    private String problemDescription;
    private String address;
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Problem_Model() {
    }

    public Problem_Model(String name, Uri userimg, Uri problemimg, String problemDescription, String address, String id, String problemsType) {
        this.userimg = userimg.toString();
        this.name = name;
        this.problemimg = problemimg.toString();
        this.problemDescription = problemDescription;
        this.address = address;
        this.id = id;
        this.problemsType=problemsType;
    }

    public Problem_Model(String name, Uri userimg, String problemDescription, String address, String id, String problemsType) {
        this.name = name;
        this.userimg = userimg.toString();
        this.problemDescription = problemDescription;
        this.id = id;
        this.address=address;
        this.problemsType=problemsType;
    }
    public Problem_Model(String name, String problemimg, String problemDescription, String address, String id, String problemsType) {
        this.name = name;
        this.problemimg = problemimg;
        this.address=address;
        this.problemDescription = problemDescription;
        this.id = id;
        this.problemsType=problemsType;
    }



    public Problem_Model(String name, String problemDescription, String address, String id,String problemsType) {
        this.name = name;
        this.problemDescription = problemDescription;
        this.address = address;
        this.id = id;
        this.problemsType=problemsType;
    }

    public void setProblemimg(String problemimg) {
        this.problemimg = problemimg;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getUserimg() {
        return userimg;
    }

    public String getName() {
        return name;
    }


    public String getProblemimg() {
        return problemimg;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public String getAddress() {
        return address;
    }
}
