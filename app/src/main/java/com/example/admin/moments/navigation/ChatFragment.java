package com.example.admin.moments.navigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.admin.moments.Utils;
import com.example.admin.moments.models.Messages;
import com.example.admin.moments.R;
import com.example.admin.moments.settings.SettingsActivity;
import com.example.admin.moments.settings.StatusActivity;
import com.example.admin.moments.signing.CheckCodeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ChatFragment extends Fragment {

    private DatabaseReference mReference;
    private DatabaseReference mRef;
    private DatabaseReference mRefSendMessage;
    private DatabaseReference mRefMessages;
    private FirebaseAuth mAuth;
    String mUser;
    public static final String TAG="chat fragment";
    private ImageButton mSend;
    private ImageButton mAdd;
    private EditText mText;
    private OnFragmentInteractionListener mListener;
    RecyclerView mRecycle;
    private List<Messages> messageList=new ArrayList<>();
    LinearLayoutManager  mLinear;
    private MessagesAdapter mAdapter;

    private static final int itemsToLoad=10;
    private int mCurrentPage=1;
    private SwipeRefreshLayout mRefresh;
    private  int itemPos=0;
    private String mLastKey="";
    private String mPrevKey="";
    String messageNumber;
    public ChatFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chat, container, false);
        if (mListener != null) {
            mListener.onFragmentInteraction("Chat");
        }
        mAuth = FirebaseAuth.getInstance();

        mAdd=view.findViewById(R.id.PickerButton);
        mSend=view.findViewById(R.id.sendButton);
        mText=view.findViewById(R.id.messageEditText);

        mAdapter=new MessagesAdapter(getActivity(),messageList);
        mRefresh=view.findViewById(R.id.swipeLayout);
        mRecycle=view.findViewById(R.id.messages_list);
        mLinear=new LinearLayoutManager(getActivity());
        mRecycle.setHasFixedSize(true);
        mRecycle.setLayoutManager(mLinear);
        mRecycle.setAdapter(mAdapter);


        loadMessages();

        //not yet
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        // send
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }


        });
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                itemPos=0;
                loadMoreMessages();

            }


        });

        return view;
    }

    private void loadMoreMessages() {

        if(mAuth.getCurrentUser()!=null ){
            mReference= FirebaseDatabase.getInstance().getReference().child("Couples");
            mUser=mAuth.getCurrentUser().getUid();
            String prefs=PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Utils.COUPLE_KEYCODE,"");
            final DatabaseReference mRefMessages=mReference.child(prefs).child("chat").child("messages");
            final Query messageQuery=mRefMessages.orderByKey().endAt(mLastKey).limitToLast(10);

                    messageQuery.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Messages message=dataSnapshot.getValue(Messages.class);
                            String messageKey=dataSnapshot.getKey();
                            if(!mPrevKey.equals(messageKey)){
                                messageList.add(itemPos++,message);

                            }else{
                                mPrevKey=mLastKey;
                            }
                            if(itemPos==1){
                                mLastKey=messageKey;
                            }
                            mAdapter=new MessagesAdapter(getActivity(),messageList);
                            mRecycle.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            mRefresh.setRefreshing(false);
                            mLinear.scrollToPositionWithOffset(10,0);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

    }

    private void loadMessages() {
        if(mAuth.getCurrentUser()!=null ){
            mReference= FirebaseDatabase.getInstance().getReference().child("Couples");
            mUser=mAuth.getCurrentUser().getUid();
            String prefs=PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Utils.COUPLE_KEYCODE,"");

            mRefMessages=mReference.child(prefs).child("chat").child("messages");
            final Query messageQuery=mRefMessages.limitToLast(mCurrentPage*itemsToLoad);

                    messageQuery.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Messages messageData=dataSnapshot.getValue(Messages.class);
                            String messageKey=dataSnapshot.getKey();
                            itemPos++;
                            if(itemPos==1){
                                mLastKey=messageKey;
                                mPrevKey=messageKey;
                            }
                            messageList.add(messageData);
                            mAdapter=new MessagesAdapter(getActivity(),messageList);
                            mRecycle.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            mRecycle.scrollToPosition(messageList.size()-1);
                            mRefresh.setRefreshing(false);

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }


    }

    private void sendMessage() {
        final String message=mText.getText().toString();
        if(!TextUtils.isEmpty(message)){
            if(mAuth.getCurrentUser()!=null ) {
                mReference = FirebaseDatabase.getInstance().getReference().child("Couples");
                String prefs=PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Utils.COUPLE_KEYCODE,"");
                mRefSendMessage=mReference.child(prefs).child("chat").child("messages");
                mRefSendMessage.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long messageNum= 1;
                        messageNum +=dataSnapshot.getChildrenCount();
                        messageNumber= String.valueOf(messageNum);
                        mRefSendMessage=mRefSendMessage.child(messageNumber);
                        Map messageMap = new HashMap();
                        messageMap.put("messages", message);
                        messageMap.put("seen", false);
                        messageMap.put("type", "text");
                        messageMap.put("from", mUser);
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        mRefSendMessage.setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d(TAG," add it");
                                }else{
                                    Log.d(TAG,"doesn't add it");
                                }
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mText.setText("");


            }

        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title );
    }
}
