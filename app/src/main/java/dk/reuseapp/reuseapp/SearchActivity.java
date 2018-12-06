package dk.reuseapp.reuseapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class SearchActivity extends Activity {
    private ArrayList<PostInfo> postInfoArrayList;
    private ArrayList<PostInfo> postsForView;
    private DatabaseReference fdatabase;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private String searchFilter;
    private SeekBar geoSlider;
    private TextView maxDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        fdatabase = FirebaseDatabase.getInstance().getReference().child("Post");
        final SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*searchFilter = query;
                applyFilter();*/
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchFilter = newText;
                applyFilter();
                return false;
            }
        });
        /*int searchCloseButtonId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = searchView.findViewById(searchCloseButtonId);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery("",true);
            }
        });*/

        searchFilter = null;
        postInfoArrayList = new ArrayList();
        postsForView = new ArrayList<>();
        getAllPosts();

        maxDistance = findViewById(R.id.distanceView);
        setDistanceView(0);
        geoSlider = findViewById(R.id.slider);
        geoSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setDistanceView(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                ;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ;
            }
        });
    }

    private void setDistanceView(int progress) {
        double max = 0.5 + 0.5 * progress;
        String distance = max + " km";
        maxDistance.setText(distance);
        applyDistanceFilter(max);
    }

    public void getAllPosts(){
        fdatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                PostInfo postInfo = dataSnapshot.getValue(PostInfo.class);
                postInfo.id = Long.parseLong(dataSnapshot.getKey());
                if(!postInfoArrayList.contains(postInfo)){
                    postInfoArrayList.add(postInfo);
                    Collections.sort(postInfoArrayList);
                    addPostForView(postInfo, true);
                    //recyclerViewAdapter.notifyItemInserted(0);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchActivity.this, databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
        setUpRecyclerView();
    }

    private void applyDistanceFilter(double distance) {
        ;
    }

    private void applyFilter() {
        //If there is no real search query we simply reset.
        int previousSize = postsForView.size();
        if (searchFilter == null || searchFilter.equals("") || searchFilter.trim().equals("")) {
            postsForView.clear();
            postsForView.addAll(postInfoArrayList);
        }
        else {
            ArrayList<PostInfo> newList = new ArrayList<>();
            for (PostInfo p : postInfoArrayList) {
                if (p.getTitle().contains(searchFilter)) {
                    newList.add(p);
                }
            }
            postsForView.clear();
            postsForView.addAll(newList);
            Collections.sort(postsForView);
        }
//        if (postsForView.size() < previousSize) {
//            recyclerViewAdapter.notifyItemRangeChanged(0,
//                    postsForView.size() - 1);
//            recyclerViewAdapter.notifyItemRangeRemoved(postsForView.size(),
//                    previousSize - 1);
//        }
//        else {
//            recyclerViewAdapter.notifyItemRangeChanged(0, previousSize - 1);
//        }
//        if (postsForView.size() > previousSize) {
//            recyclerViewAdapter.notifyItemRangeInserted(previousSize,
//                    postsForView.size()-1);
//        }
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private void addPostForView(PostInfo post, boolean sort) {
        if (searchFilter == null || post.getTitle().contains(searchFilter)) {
            postsForView.add(post);
            if (sort) {
                Collections.sort(postsForView);
            }
            recyclerViewAdapter.notifyItemInserted(postsForView.indexOf(post));
        }
    }

    public void setUpRecyclerView(){
        recyclerView = findViewById(R.id.recyclerviewid);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewAdapter = new RecyclerViewAdapter(postsForView,this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}
