package com.example.admin.moments;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.admin.moments.models.MomentDate;
import com.example.admin.moments.navigation.MediaFragment;
import com.example.admin.moments.navigation.NavigationActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by ADMIN on 3/8/2018.
 */


public class FirebaseService extends IntentService {
    public static final String ACTION_GET_MEDIA = "com.noga.moments.action.GET_MEDIA";
    public static final String ACTION_ALARM = "com.noga.moments.action.ALARM";

    // pendingIntent requests
    public static final int NOTIFICATION_REQUEST = 0;
    public static final int ALARM_REQUEST = 1;

    DatabaseReference mRef;

    public FirebaseService() {
        super("FirebaseService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_MEDIA.equals(action)) {
                handleActionGetMedia();
            }

        }
    }



    private void handleActionGetMedia() {
        mRef = FirebaseDatabase.getInstance().getReference();
       // String couplesKeycode = PreferenceManager.getDefaultSharedPreferences(this).getString(Utils.COUPLE_KEYCODE, "");
         String couplesKeycode=Utils.getCoupleCode(getApplicationContext());
        mRef.child(Utils.CHILD_COUPLES).child(couplesKeycode).child(Utils.CHILD_CHAT).child(Utils.CHILD_MEDIA)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> images = dataSnapshot.getChildren().iterator();
                        ArrayList<Uri> photosUris = new ArrayList<>();
                        while (images.hasNext()) {
                            try {
                                DataSnapshot dataSnapshot1=images.next();
                                String data=  dataSnapshot1.child(Utils.MEDIA_URL_KEY).getValue().toString();
                                Log.e("null url",data);
                                Uri photoUrl = Uri.parse(data);
                                photosUris.add(photoUrl);
                            }catch (NullPointerException e){
                                Log.e("null url",e.toString());
                            }
                        }
                        Intent result = new Intent(MediaFragment.GET_MEDIA_ACTION);
                        result.putParcelableArrayListExtra(Intent.EXTRA_RETURN_RESULT, photosUris);
                        sendBroadcast(result);
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}
