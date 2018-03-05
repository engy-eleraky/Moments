package com.example.admin.moments.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.moments.R;
import com.example.admin.moments.models.Timeline;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by ADMIN on 3/4/2018.
 */

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {

    private ArrayList<Timeline> posts;
    private Context context;

    public TimelineAdapter(ArrayList<Timeline> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    public void addNewPost(Timeline post) {
        posts.add(post);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TimelineViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.timeline_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
        Timeline post = posts.get(position);

        holder.dateTextView.setText(post.date);
        Picasso.with(context).load(post.image).into(holder.postImageView);
        holder.postPreview.setText(post.post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class TimelineViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        ImageView postImageView;
        TextView postPreview;

        public TimelineViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.timestampTextView);
            postImageView = itemView.findViewById(R.id.timelineImageView);
            postPreview = itemView.findViewById(R.id.postPreview);
        }
    }
}

