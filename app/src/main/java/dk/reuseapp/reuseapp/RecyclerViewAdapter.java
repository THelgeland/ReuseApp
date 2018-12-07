package dk.reuseapp.reuseapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
        return new RecyclerViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder recyclerViewHolder, int i) {
        final PostInfo postInfo = postInfoArrayList.get(i);
        recyclerViewHolder.date.setText(postInfo.getDate());

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        Glide.with(context).load(postInfo.getPicture()).apply(options).
                into(recyclerViewHolder.picture);
        recyclerViewHolder.title.setText(postInfo.getTitle());
        recyclerViewHolder.postcontainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ResultActivity.class);
                intent.putExtra("PostInfo", postInfo);
                context.startActivity(intent);
                /*final Dialog nagDialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                nagDialog.setCancelable((false));
                nagDialog.setContentView(R.layout.post_container);

                ImageView nagDialogImageView = nagDialog.findViewById(R.id.picturecontainerID);
                TextView nagDialogTextView = nagDialog.findViewById(R.id.postcontainerDescriptionID);

                nagDialogImageView.setImageDrawable(recyclerViewHolder.picture.getDrawable());
                nagDialogTextView.setText(recyclerViewHolder.description.getText());
                nagDialog.show();

                nagDialog.setOnKeyListener(new Dialog.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            nagDialog.dismiss();
                        }
                        return true;
                    }
                });*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return postInfoArrayList.size();
    }


    static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private TextView date;
        private ImageView picture;
        private LinearLayout postcontainer;
        private TextView title;

        RecyclerViewHolder(View view){
            super(view);
            date=view.findViewById(R.id.dateid);
            picture=view.findViewById(R.id.pictureID);
            title=view.findViewById(R.id.titleid);
            postcontainer=view.findViewById(R.id.postcontainer);


        }
    }

}
