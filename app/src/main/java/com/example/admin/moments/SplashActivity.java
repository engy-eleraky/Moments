package com.example.admin.moments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.admin.moments.models.MomentDate;
import com.example.admin.moments.navigation.NavigationActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;


public class SplashActivity extends AppCompatActivity {
Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler= new Handler();
        handler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        Intent mainIntent = new Intent(SplashActivity.this, NavigationActivity.class);
//                        setNearestAlarmActive(context);
                        startActivity(mainIntent);
                        finish();
                    }//run
                },3000);


    }

   /* public static void setNearestAlarmActive(final Context context) {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        String coupleCode = Utils.getCoupleCode(context);
        mRef.child(Utils.CHILD_COUPLES).child(coupleCode).child(Utils.CHILD_CALENDAR).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                MomentDate nearestDate = null;
                while(iterator.hasNext()) {
                    MomentDate momentDate = iterator.next().getValue(MomentDate.class);
                    if (nearestDate == null) {
                        nearestDate = momentDate;
                    } else {
                        nearestDate = Utils.getNearestMomentDate(nearestDate, momentDate);
                    }
                }
                if (nearestDate != null) {
                    setAlarm(nearestDate, context);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void setAlarm(MomentDate momentDate, Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        long dateAsMillis = Utils.getDateAsMillis(momentDate.date);

        Intent intent = new Intent(context.getApplicationContext(), FirebaseService.class);
        intent.setAction(FirebaseService.ACTION_ALARM);
        //serialazable
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, momentDate);
        PendingIntent pendingIntent = PendingIntent.getService(
                context,
                FirebaseService.ALARM_REQUEST,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        alarmManager.set(AlarmManager.RTC, dateAsMillis, pendingIntent);
    }

*/
}
