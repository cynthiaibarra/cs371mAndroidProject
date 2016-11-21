package cai288.cs371m.project.customClasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Cynthia on 11/7/2016.
 */
public class AppUser implements Serializable{

    private String name;
    private String email;
    private String uid;
    private String photo;
    protected ArrayList<String> friends;
    protected ArrayList<String> sentRequests;
    protected ArrayList<String> receivedRequests;

    public AppUser(){
    }

    public AppUser(String email, String name, String photo, String uid){
        this.email = email;
        this.name = name;
        this.photo = photo;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public String getPhoto() {
        return photo;
    }
}