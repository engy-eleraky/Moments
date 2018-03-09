package com.example.admin.moments.navigation;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.admin.moments.R;
import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppCompatActivity {
Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        ImageView image=findViewById(R.id.fullImage);
        Uri uri=getIntent().getData();

        Picasso.with(context).load(uri).into(image);

    }
}
