package com.example.admin.moments.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.admin.moments.R;
import com.example.admin.moments.Utils;
import com.example.admin.moments.models.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ADMIN on 3/5/2018.
 */

public class ChatWidgetRemotViewService  extends RemoteViewsService {
    private FirebaseAuth mAuth;
    private String mUser;
    private DatabaseReference mReference;
    private DatabaseReference mRefSendMessage;
    private DatabaseReference mRefMessages;
    private Messages message;


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyWidgetRemoteViewsFactory(this.getApplicationContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    class MyWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{
        private Context context;
        private List<Messages> messagesList;
        private CountDownLatch mCountDownLatch;


        public MyWidgetRemoteViewsFactory(Context context) {
            this.context = context;
            messagesList=new ArrayList<>();

        }
        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {



            mCountDownLatch = new CountDownLatch(1);
            loadMessagesWidget();
            try {
                mCountDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        private void loadMessagesWidget(){
            if(mCountDownLatch.getCount()==0){

            }else {
                //load data
                mAuth = FirebaseAuth.getInstance();
                mUser = mAuth.getCurrentUser().getUid();
                if (mAuth.getCurrentUser() != null) {

                    mReference = FirebaseDatabase.getInstance().getReference().child(Utils.CHILD_COUPLES);
                    String prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Utils.COUPLE_KEYCODE, "");

                    mRefMessages = mReference.child(prefs).child(Utils.CHILD_CHAT).child(Utils.CHILD_MESSAGES);

                    mRefMessages.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> messages = dataSnapshot.getChildren().iterator();
                            while (messages.hasNext()) {
                                message = messages.next().getValue(Messages.class);

                                messagesList.add(message);


                            }
                            mCountDownLatch.countDown();


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if (messagesList != null) {
                return messagesList.size();
            } else return 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_view_item);
            Messages messages=messagesList.get(position);
            String messageData=messages.getMessages();
            String textTime=messages.getTime();
            String from=messages.getFrom();
            if(from.equals(mUser)) {
                views.setTextColor(R.id.messageWidget, getResources().getColor(R.color.pink));
                views.setTextColor(R.id.timeWidget, getResources().getColor(R.color.pink));
            }
            else{
                views.setTextColor(R.id.messageWidget, getResources().getColor(R.color.cardview_dark_background));
                views.setTextColor(R.id.timeWidget, getResources().getColor(R.color.cardview_dark_background));
            }
            views.setTextViewText(R.id.messageWidget,messageData);
            views.setTextViewText(R.id.timeWidget,textTime);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

    }



}