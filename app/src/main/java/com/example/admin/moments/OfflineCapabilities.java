package com.example.admin.moments;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by ADMIN on 2/20/2018.
 */

public class OfflineCapabilities extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
         /*picasso*/
        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built=builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

    }
}