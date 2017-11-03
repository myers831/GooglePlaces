package com.example.admin.googleplaces;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.admin.googleplaces.model.Result;

public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "Tag2";
    TextView tvTitle, tvLocation;
    ImageView ivPhoto;
//    RecyclerView rvPictures;
//    RecyclerView.LayoutManager layoutManager;
//    RecycleViewPictureAdapter adapter;
    Result result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        tvTitle = findViewById(R.id.tvTitle);
        tvLocation = findViewById(R.id.tvLocation);
        ivPhoto = findViewById(R.id.ivPhoto);
        //rvPictures = findViewById(R.id.rvPictures);

        result = (Result) getIntent().getSerializableExtra("result");
        Log.d(TAG, "onCreate: " + result.getName());

        tvTitle.setText(result.getName().toString());
        tvLocation.setText(result.getVicinity());
        Glide.with(this).load(result.getIcon()).into(ivPhoto);
//        layoutManager = new GridLayoutManager(this, 3);
//        adapter = new RecycleViewPictureAdapter(result.getPhotos());
//        rvPictures.setLayoutManager(layoutManager);
//        rvPictures.setAdapter(adapter);

    }
}
