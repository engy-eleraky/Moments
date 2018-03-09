package com.example.admin.moments.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.admin.moments.R;
import com.example.admin.moments.navigation.OnImageSelectedListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by ADMIN on 3/8/2018.
 */

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {

    private ArrayList<Uri> mediaImages = new ArrayList<>();
    private Context context;
    private OnImageSelectedListener mListener;

    public MediaAdapter(ArrayList<Uri> mediaImages, Context context, OnImageSelectedListener mListener) {
        this.mediaImages = mediaImages;
        this.context = context;
        this.mListener = mListener;
    }

    @Override
    public MediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MediaViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.media_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MediaViewHolder holder, int position) {
        mediaImages.get(position);
        Picasso.with(context).load(mediaImages.get(position)).placeholder(R.drawable.icon).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mediaImages.size();
    }

    class MediaViewHolder extends ViewHolder implements View.OnClickListener {
        ImageView imageView;
        public MediaViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageMedia);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onImageSelected(mediaImages.get(getAdapterPosition()));
        }
    }
}

