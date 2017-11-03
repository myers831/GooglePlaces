package com.example.admin.googleplaces;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.admin.googleplaces.model.Photo;
import com.example.admin.googleplaces.model.Result;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Admin on 10/14/2017.
 */

public class RecycleViewPictureAdapter extends RecyclerView.Adapter<RecycleViewPictureAdapter.ViewHolder> {

    List<Photo> photoList = new ArrayList<>();
    Context context;

    public RecycleViewPictureAdapter(List<Photo> photoList) {
        this.photoList = photoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Photo photo = photoList.get(position);
        holder.photo = photo;

        Glide.with(context).load(photo.getHtmlAttributions().get(0)).into(holder.ivPhoto);

    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Photo photo;
        ImageView ivPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
/*
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ItemViewActivity.class);
                    intent.putExtra("result", result);
                    context.startActivity(intent);
                }
            });
    */

            ivPhoto = itemView.findViewById(R.id.ivPhoto);
        }
    }
}
