package dk.reuseapp.reuseapp;

import com.google.android.gms.common.api.Api;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;


/**
 * This is the java post class which contains informations regarding a post.
 *
 *
 * @author Henrik Tran
 */
public class PostInfo {
    //private FirebaseDatabase storage = FirebaseStorage.getInstance();
    public String location;
    public String description;
    public String picture;
    public String title;
    public String date;
    public long id;

    public PostInfo(String location, String description, String picture,String title,String date){
        this.location=location;
        this.description = description;
        this.picture= picture;
        this.title = title;
        this.date= date;
    }

    public PostInfo(){

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
    public String getDate(){
        return date;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("description", description);
        result.put("date", date);
        result.put("location", location);
        result.put("picture", picture);
        result.put("title", title);
        result.put("id", id);
        return result;
    }
}
