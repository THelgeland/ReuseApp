package dk.reuseapp.reuseapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * This is the java post class which contains informations regarding a post.
 *
 *
 * @author Henrik Tran
 */
public class PostInfo {
    //private FirebaseDatabase storage = FirebaseStorage.getInstance();
    private String name;
    private String email;
    private String location;
    private String text;
    private String picture;
    private String title;

    public PostInfo(String name,String email, String location, String text, String picture,String title){
        this.name=name;
        this.email=email;
        this.location=location;
        this.text= text;
        this.picture= picture;
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
