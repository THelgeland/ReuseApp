package dk.reuseapp.reuseapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * This is the java post class which contains information regarding a post.
 *
 *
 * @author Henrik Tran
 */
public class PostInfo {
    private String location;
    private String description;
    private String picture;
    private String title;

    public PostInfo(String title, String description, String location, String picture){
        this.location=location;
        this.description= description;
        this.picture= picture;
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
