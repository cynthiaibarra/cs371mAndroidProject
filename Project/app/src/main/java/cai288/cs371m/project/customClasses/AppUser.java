package cai288.cs371m.project.customClasses;

/**
 * Created by Cynthia on 11/7/2016.
 */
public class AppUser {
    public String name;
    public String email;
    public String uid;
    public String photo;

    public AppUser(){
    }

    public AppUser(String email, String name, String photo, String uid){
        this.email = email;
        this.name = name;
        this.photo = photo;
        this.uid = uid;
    }
}
