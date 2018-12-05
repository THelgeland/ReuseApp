package dk.reuseapp.reuseapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter <RecyclerViewAdapter.RecyclerViewHolder> {
    private ArrayList<PostInfo> postInfoArrayList;
    private Context context;

    public RecyclerViewAdapter(ArrayList<PostInfo> postInfoArrayList,Context context){
        this.postInfoArrayList=postInfoArrayList;
        this.context=context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        System.out.println("go");
        return new RecyclerViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int i) {
        PostInfo postInfo = postInfoArrayList.get(i);
        System.out.println("Title: " + postInfo.title);
        recyclerViewHolder.description.setText(postInfo.getDescription());
        recyclerViewHolder.date.setText(postInfo.getDate());

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        Glide.with(context).load(postInfo.getPicture()).apply(options).
                into(recyclerViewHolder.picture);
        recyclerViewHolder.title.setText(postInfo.getTitle());
        recyclerViewHolder.postcontainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
                //startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postInfoArrayList.size();
    }


    static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private TextView description;
        private TextView date;
        private ImageView picture;
        private LinearLayout postcontainer;
        private TextView title;

        RecyclerViewHolder(View view){
            super(view);
            description=view.findViewById(R.id.descriptionid);
            date=view.findViewById(R.id.dateid);
            picture=view.findViewById(R.id.pictureID);
            title=view.findViewById(R.id.titleid);
            postcontainer=view.findViewById(R.id.postcontainer);


        }
    }

}
