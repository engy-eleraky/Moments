package com.example.admin.moments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.example.admin.moments.models.MomentDate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Iterator;

/**
 * Created by ADMIN on 3/3/2018.
 */


public class Utils {

    // FirebaseDatabase Children
    public static final String CHILD_COUPLES = "Couples";
    public static final String CHILD_ONWAIT = "OnWait";
    public static final String CHILD_USERS = "Users";

    public static final String PARTNER1_ID = "partner1";
    public static final String PARTNER2_ID = "partner2";
    public static final String ID = "id";
    public static final String CODE = "code";
    //user
    public static final String NAME = "name";
    public static final String IMAGE = "image";
    public static final String EMAIL = "email";
    public static final String STATUS = "status";
    public static final String THUMBNAIL = "thumbnail";

    //chat
    public static final String CHILD_MESSAGES = "messages";
    public static final String CHILD_CHAT = "chat";
    public static final String CHILD_PROFILE_STORAGE = "profiles";
    public static final String MESSAGE= "messages";
    public static final String TIME= "time";
    public static final String TYPE = "type";
    public static final String SEEN = "seen";
    public static final String FROM = "from";

    //media
    public static final String CHILD_MEDIA = "media";
    public static final String CHILD_MEDIA_STORAGE = "Media";
    public static final String MEDIA_URL_KEY = "url";


    // Calendar
    public static final String MOMENT_DATE_TITLE = "title";
    public static final String MOMENT_DATE_DATE = "date";
    public static final String MOMENT_DATE_REMIND = "remind";
    public static final String CHILD_CALENDAR = "Calendar";

    // Timeline
    public static final String MOMENT_POST = "post";
    public static final String MOMENT_FROM = "from";
    public static final String MOMENT_DATE = "date";
    public static final String MOMENT_IMAGE = "image";
    public static final String CHILD_TIMELINE = "Timeline";

    // shared preference keys
    public static final String COUPLE_KEYCODE = "couple_keycode";
    public static final String MESSAGE_LIST = "message_list";

    //progress dialogue
    public static final String WAIT = "please wait.....";
    public static final String UPLOAD_PHOTO= "uploading photo";
    public static final String REGISTER_MESSAGE = "Registering user....";
    public static final String LOGGING_MESSAGE = "Logging user....";
    public static final String FIRST_TIME = "firstTime";
    public static final String SAVE_CHANGES = "saving changes";


    //toasts
    public static final String FAIL_LAUNCH = "fail to launch";
    public static final String FAIL_AUTHENTICATION = "Authentication failed";
    public static final String ENTER = "please enter your email/passward/name";
    public static final String CONNECT = "Connecting you with your partner...";
    public static final String WELCOME = "Welcome to chat with your partner :)";
    public static final String WORK = "working";
    public static final String NOT_WORK = "not working";


    public static final String ID_ADDED = "id added";
    public static final String ID1_ADDED = "id1 added";
    public static final String ID1_FAILED = "id1 failed ";
    public static final String ID_FAILED = "id failed ";
    public static final String CODE_ADDED= "code added";
    public static final String CODE_FAILED = "code failed";


    public static final String REGISTER= "register";
    public static final String LOGIN= "LogIn";

    //get couple code
    public static String getCoupleCode(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(COUPLE_KEYCODE, "");
    }

   /* public static long getDateAsMillis(String dateAsString) {
        String[] dateSplit = dateAsString.split("/");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.getInteger(dateSplit[2]), Integer.getInteger(dateSplit[1]), Integer.getInteger(dateSplit[0]));
        return calendar.getTimeInMillis();
    }

    public static MomentDate getNearestMomentDate(MomentDate moment1, MomentDate moment2) {
        if (getDateAsMillis(moment1.date) != getDateAsMillis(moment2.date)) {
            return getDateAsMillis(moment1.date) < getDateAsMillis(moment2.date) ? moment1: moment2;
        }
        return null;
    }

    public static void setNearestAlarmActive(final Context context) {
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

