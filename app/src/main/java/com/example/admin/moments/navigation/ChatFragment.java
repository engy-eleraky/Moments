package com.example.admin.moments.navigation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.admin.moments.Utils;
import com.example.admin.moments.adapters.MessagesAdapter;
import com.example.admin.moments.models.Messages;
import com.example.admin.moments.R;
import com.example.admin.moments.settings.SettingsActivity;
import com.example.admin.moments.widget.ChatWidgetProvider;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

//import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;


public class ChatFragment extends Fragment {

    private DatabaseReference mReference;
    private DatabaseReference mRef;
    private DatabaseReference mRefSendMessage;
    private DatabaseReference mRefMessages;
    private FirebaseAuth mAuth;
    String mUser;
    public static final String TAG = "chat fragment";
    private ImageButton mSend;
    private ImageButton mAdd;
    private EditText mText;
    private OnFragmentInteractionListener mListener;
    RecyclerView mRecycle;
    private List<Messages> messageList = new ArrayList<>();
    LinearLayoutManager mLinear;
    private MessagesAdapter mAdapter;

    private static final int itemsToLoad = 10;
    private int mCurrentPage = 1;
    private SwipeRefreshLayout mRefresh;
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";
    String messageNumber = "";
    private static final int GALLERY_PICK = 1;
    private StorageReference mStorageRef;
    private ProgressDialog mDialogue;

    String code = "";
    String prefs = "";
    // String downloadUrl="";
    String current_date = "";
    private static final String SAVED_LAYOUT1_MANAGER = "Layout";
    Parcelable layout1;

    public ChatFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(SAVED_LAYOUT1_MANAGER, mRecycle.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            layout1 = savedInstanceState.getParcelable(SAVED_LAYOUT1_MANAGER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        if (mListener != null) {
            mListener.onFragmentInteraction(Utils.CHILD_CHAT);
        }
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mAdd = view.findViewById(R.id.PickerButton);
        mSend = view.findViewById(R.id.sendButton);
        mText = view.findViewById(R.id.messageEditText);
        mDialogue = new ProgressDialog(getActivity());

        mAdapter = new MessagesAdapter(getActivity(), messageList);
        mRefresh = view.findViewById(R.id.swipeLayout);
        mRecycle = view.findViewById(R.id.messages_list);
        mLinear = new LinearLayoutManager(getActivity());
        mRecycle.setHasFixedSize(true);
        mRecycle.setLayoutManager(mLinear);
        mRecycle.setAdapter(mAdapter);
        //generate code
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            code += String.valueOf(random.nextInt(10));
        }
        loadMessages();

        //add
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

            }
        });
        // send
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String message = mText.getText().toString();
                String current_date = (DateFormat.getDateTimeInstance().format(new Date()));
                boolean seen = false;
                String type = "text";
                mUser = mAuth.getCurrentUser().getUid();
                Messages newMessage = new Messages(message, seen, type, mUser, current_date);
                sendMessage(newMessage);
            }


        });
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                itemPos = 0;
                loadMoreMessages();

            }


        });

        return view;
    }

    //map
    private void sendMessage(final Messages messages) {

        if (mAuth.getCurrentUser() != null) {
            mReference = FirebaseDatabase.getInstance().getReference().child(Utils.CHILD_COUPLES);
            // String prefs=PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Utils.COUPLE_KEYCODE,"");
            String prefs = Utils.getCoupleCode(getActivity());
            mRefSendMessage = mReference.child(prefs).child(Utils.CHILD_CHAT).child(Utils.CHILD_MESSAGES);
            mRefSendMessage.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override

                public void onDataChange(DataSnapshot dataSnapshot) {
                    long messageNum = dataSnapshot.getChildrenCount();
                    messageNumber = String.valueOf(messageNum + 1);

                    mRefSendMessage = mRefSendMessage.child(messageNumber);
                    Map messageMap = new HashMap();
                    messageMap.put(Utils.MESSAGE, messages.getMessages());
                    messageMap.put(Utils.SEEN, messages.isSeen());
                    messageMap.put(Utils.TYPE, messages.getType());
                    messageMap.put(Utils.FROM, mUser);
                    messageMap.put(Utils.TIME, messages.getTime());
                    mRefSendMessage.setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, " add it");
                            } else {
                                Log.d(TAG, "doesn't add it");
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

    private void loadMoreMessages() {

        if (mAuth.getCurrentUser() != null) {
            mReference = FirebaseDatabase.getInstance().getReference().child(Utils.CHILD_COUPLES);
            mUser = mAuth.getCurrentUser().getUid();
            // String prefs=PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Utils.COUPLE_KEYCODE,"");
            String prefs = Utils.getCoupleCode(getActivity());
            final DatabaseReference mRefMessages = mReference.child(prefs).child(Utils.CHILD_CHAT).child(Utils.CHILD_MESSAGES);
            final Query messageQuery = mRefMessages.orderByKey().endAt(mLastKey).limitToLast(10);

            messageQuery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Messages message = dataSnapshot.getValue(Messages.class);
                    String messageKey = dataSnapshot.getKey();
                    if (!mPrevKey.equals(messageKey)) {
                        messageList.add(itemPos++, message);

                    } else {
                        mPrevKey = mLastKey;
                    }
                    if (itemPos == 1) {
                        mLastKey = messageKey;
                    }
                    mAdapter = new MessagesAdapter(getActivity(), messageList);
                    mRecycle.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    mRefresh.setRefreshing(false);
                    mLinear.scrollToPositionWithOffset(10, 0);
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

    //show
    private void loadMessages() {
        if (mAuth.getCurrentUser() != null) {

            mReference = FirebaseDatabase.getInstance().getReference().child(Utils.CHILD_COUPLES);
            mUser = mAuth.getCurrentUser().getUid();
            // String prefs=PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Utils.COUPLE_KEYCODE,"");
            String prefs = Utils.getCoupleCode(getActivity());
            mRefMessages = mReference.child(prefs).child(Utils.CHILD_CHAT).child(Utils.CHILD_MESSAGES);
            final Query messageQuery = mRefMessages.limitToLast(mCurrentPage * itemsToLoad);

            messageQuery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Messages messageData = dataSnapshot.getValue(Messages.class);
                    String messageKey = dataSnapshot.getKey();
                    itemPos++;
                    if (itemPos == 1) {
                        mLastKey = messageKey;
                        mPrevKey = messageKey;
                    }
                    messageList.add(messageData);
                   // getPreferences(messageList);
                    mAdapter = new MessagesAdapter(getActivity(), messageList);
                    mRecycle.setAdapter(mAdapter);
                    // mAdapter.notifyDataSetChanged();
                    if (layout1 != null) {
                        restoreLayoutManagerPosition();
                    } else {
                        mRecycle.scrollToPosition(messageList.size() - 1);

                    }
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            mDialogue.setTitle(Utils.UPLOAD_PHOTO);
            mDialogue.setMessage(Utils.WAIT);
            mDialogue.show();

            final Uri resultUri = data.getData();
            if (mAuth.getCurrentUser() != null) {
                mReference = FirebaseDatabase.getInstance().getReference().child(Utils.CHILD_COUPLES);
                //prefs=PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Utils.COUPLE_KEYCODE,"");
                prefs = Utils.getCoupleCode(getActivity());
                mRefSendMessage = mReference.child(prefs).child(Utils.CHILD_CHAT).child(Utils.CHILD_MESSAGES);
                mRef = mReference.child(prefs).child(Utils.CHILD_CHAT).child(Utils.CHILD_MEDIA);
                mUser = mAuth.getCurrentUser().getUid();


                final StorageReference filePath = mStorageRef.child(Utils.CHILD_MEDIA_STORAGE).child(prefs).child(code + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            mRefSendMessage.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long messageNum = dataSnapshot.getChildrenCount();
                                    messageNumber = String.valueOf(messageNum + 1);
                                    current_date = (DateFormat.getDateTimeInstance().format(new Date()));

                                    mRefSendMessage = mRefSendMessage.child(messageNumber);
                                    Map messageMap = new HashMap();
                                    messageMap.put(Utils.MESSAGE, downloadUrl);
                                    messageMap.put(Utils.SEEN, false);
                                    messageMap.put(Utils.TYPE, Utils.IMAGE);
                                    messageMap.put(Utils.FROM, mUser);
                                    messageMap.put(Utils.TIME, current_date);
                                    mRefSendMessage.setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mDialogue.hide();
                                                Log.d(TAG, " add it to messages");
                                            } else {
                                                Log.d(TAG, "doesn't add it to messages");
                                            }
                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            mText.setText("");

                            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    Map mediaMap = new HashMap();
                                    mediaMap.put(Utils.MEDIA_URL_KEY, downloadUrl);
                                    //mediaMap.put(Utils.TIME, current_date);
                                    mRef.child(messageNumber).setValue(mediaMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, " add it to media");
                                            } else {
                                                Log.d(TAG, "doesn't add it to media");
                                            }
                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                });


            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chat_fragment_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.widgetId) {

            sendBroadcast();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendBroadcast() {

        Intent intent = new Intent(getActivity(), ChatWidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE\"");
        getActivity().sendBroadcast(intent);
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
        void onFragmentInteraction(String title);
    }

    private void restoreLayoutManagerPosition() {
        if (layout1 != null) {
            mRecycle.getLayoutManager().onRestoreInstanceState(layout1);

        }
    }






}
