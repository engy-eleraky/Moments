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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    public String nameUser;
    public String imageUser;
    public String idUser;
    public String emailUser;
    public String nameChat;
    public String imageChat;
    private String idChat;
     private String id;
    public String emailChat;
    private DatabaseReference mReference;
    private DatabaseReference mRef;
    private DatabaseReference mRefSendMessage;
    DatabaseReference mRefMessages;
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
    public static final String SAVE_ID="save_id";
    String idChat1;
    String coupleNumber;
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
        Intent intent = getActivity().getIntent();
        id=intent.getStringExtra(CheckCodeActivity.PARTNER_ID);
        getPreference(id);
        idChat=PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SAVE_ID,"");
        //  idChat=NavigationActivity.idChat1;
       // getPreference(mUser);
      //  idUser=PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("save_id","");

        mAdapter=new MessagesAdapter(getActivity(),messageList);
        mRefresh=view.findViewById(R.id.swipeLayout);
        mRecycle=view.findViewById(R.id.messages_list);
        mLinear=new LinearLayoutManager(getActivity());
        mRecycle.setHasFixedSize(true);
        mRecycle.setLayoutManager(mLinear);
        mRecycle.setAdapter(mAdapter);

        mAdd=view.findViewById(R.id.PickerButton);
        mSend=view.findViewById(R.id.sendButton);
        mText=view.findViewById(R.id.messageEditText);


        //map reference
        if(mAuth.getCurrentUser()!=null ){

            mReference= FirebaseDatabase.getInstance().getReference().child("Couples");
            mUser=mAuth.getCurrentUser().getUid();
            mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long coupleNum=1;
                    coupleNum +=dataSnapshot.getChildrenCount();
                    coupleNumber= String.valueOf(coupleNum);
                    mRef=mReference.child(coupleNumber);
                    HashMap<String,String> map=new HashMap<>();
                    map.put("partner1",mUser);
                    map.put("partner2",idChat);

                    mRef.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                                  if (task.isSuccessful()){
                                      Log.d(TAG," add it");

                                  }else{
                                      Log.d(TAG," doesn't add it");
                                  }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            //  loadMessages();

//            mReference.child("chat").child(mUser).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if(!dataSnapshot.hasChild(idChat)){
//
//                        Map AddMap=new HashMap();
//                        AddMap.put("seen",false);
//                        Map chatUserMap= new HashMap();
//                        chatUserMap.put("chat/" + mUser +"/"+ idChat,AddMap);
//                        chatUserMap.put("chat/" + idChat +"/"+mUser,AddMap);
//                        mReference.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
//                            @Override
//                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                if(databaseError !=null){
//                                    Log.d(TAG," doesn't has it");
//                                }
//                            }
//                        });
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
        }
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
                loadMessages();
            }


        });
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                itemPos=0;
//             messageList.clear();
                loadMoreMessages();

            }


        });

        return view;
    }

    private void loadMoreMessages() {
        ///////////////
        //not right data loading
        if(mAuth.getCurrentUser()!=null ){
            mReference= FirebaseDatabase.getInstance().getReference().child("Couples");
            mUser=mAuth.getCurrentUser().getUid();
            DatabaseReference mRefMessages=mReference.child(coupleNumber).child("chat").child("messages");
            Query messageQuery=mRefMessages.orderByKey().endAt(mLastKey).limitToLast(10);
            messageQuery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Messages messageData=dataSnapshot.getValue(Messages.class);
                    String messageKey=dataSnapshot.getKey();
                    if(!mPrevKey.equals(messageKey)){
                        messageList.add(itemPos++,messageData);

                    }else{
                        mPrevKey=mLastKey;
                    }
                    if(itemPos==1){
                        mLastKey=messageKey;
                    }



                    mAdapter=new MessagesAdapter(getActivity(),messageList);
                    mRecycle.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    //goes to bottom---fix it
                    // mRecycle.scrollToPosition(messageList.size()-1);
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
            mRefMessages=mReference.child(coupleNumber).child("chat").child("messages");
            Query messageQuery=mRefMessages.limitToLast(mCurrentPage*itemsToLoad);

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
                    //goes to bottom---fix it
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
                mRefSendMessage=mReference.child(coupleNumber).child("chat").child("messages");
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
//                String userRef = "messages/" + mUser + "/" + idChat;
//                String chatRef = "messages/" + idChat + "/" + mUser;
//                DatabaseReference messagePush = mReference.child("messages").child(mUser).child(idChat).push();
//                String pushId = messagePush.getKey();



//                Map userMessageMap = new HashMap();
//                userMessageMap.put(userRef + "/" + pushId, messageMap);
//                userMessageMap.put(chatRef + "/" + pushId, messageMap);



//                mRef.updateChildren(messageMap, new DatabaseReference.CompletionListener() {
//                    @Override
//                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                        if (databaseError != null) {
//                            Log.d(TAG, databaseError.getMessage().toString());
//                        }
//                    }
//                });


            }

            }

        }

        private void getPreference(String item){
            //save
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(SAVE_ID,item);
            editor.apply();
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
