package com.example.admin.moments.navigation;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.admin.moments.R;
import com.example.admin.moments.Utils;
import com.example.admin.moments.adapters.TimelineAdapter;
import com.example.admin.moments.models.Timeline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends Fragment {
    private ChatFragment.OnFragmentInteractionListener mListener;

    EditText postEditText;
    ImageButton addImageButton;
    ImageButton postButton;
    RecyclerView recyclerView;
    TimelineAdapter adapter;

    FirebaseAuth mAuth;
    FirebaseUser mCurrentUser;
    FirebaseStorage storage;
    StorageReference storageRef;
    DatabaseReference mRef;
    String prefs="";
    private String imageSelectedUri;
    private int GALLERY_PICK = 1;
    private ProgressDialog mDialogue;
    String code="";
    String downloadUrl="";

    public TimelineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_timeline, container, false);
        if (mListener != null) {
            mListener.onFragmentInteraction(Utils.CHILD_TIMELINE);
        }

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mRef = FirebaseDatabase.getInstance().getReference();

        postEditText = view.findViewById(R.id.timelinePostEditText);
        addImageButton = view.findViewById(R.id.addImageTimelineButton);
        postButton = view.findViewById(R.id.postButton);
        mDialogue=new ProgressDialog(getActivity());

        recyclerView = view.findViewById(R.id.timelineRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        getTimelinePosts();

        //generate code
        Random random = new Random();
        for (int i = 0 ; i < 6 ; i++) {
            code += String.valueOf(random.nextInt(10));
        }

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseImage();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String post = postEditText.getText().toString();

                if (!post.equals("")) {
                    final Calendar c = Calendar.getInstance();  // current date
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

                    String separator = "/";
                    String dateAsText = String.valueOf(dayOfMonth) + separator + String.valueOf(month+1) + separator + String.valueOf(year);

                    final Timeline newPost = new Timeline(post, mCurrentUser.getUid(), dateAsText);
                    addNewPostToDatabase(newPost);
                    adapter.addNewPost(newPost);
                    if(downloadUrl!=null){
                        newPost.setImage(downloadUrl);
                        addNewPostToDatabase(newPost);
                        adapter.addNewPost(newPost);
                    }


                }
                postEditText.setText("");
            }
        });

        return view;
    }
    //add post
    //enhanced code
    private void addNewPostToDatabase(final Timeline newPost) {
        prefs= PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Utils.COUPLE_KEYCODE,"");
        mRef.child(Utils.CHILD_COUPLES).child(prefs).child(Utils.CHILD_TIMELINE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long noOfChildren = dataSnapshot.getChildrenCount();
                Map<String, String> map = new HashMap<>();
                map.put(Utils.MOMENT_POST, newPost.post);
                map.put(Utils.MOMENT_DATE, newPost.date);
                map.put(Utils.MOMENT_FROM, newPost.from);
                ////////////////////////////
                //fix that
                if (newPost.getImage() != null) {
                    map.put(Utils.MOMENT_IMAGE, newPost.getImage());
                }

                mRef.child(Utils.CHILD_COUPLES).child(prefs).child(Utils.CHILD_TIMELINE).child(String.valueOf(noOfChildren+1)).setValue(map);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //show
    private void getTimelinePosts() {
        prefs=PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Utils.COUPLE_KEYCODE,"");
        mRef.child(Utils.CHILD_COUPLES).child(prefs).child(Utils.CHILD_TIMELINE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                ArrayList<Timeline> posts = new ArrayList<>();
                while (iterator.hasNext()) {
                    DataSnapshot timelineSnapshot = iterator.next();
                    Timeline post = timelineSnapshot.getValue(Timeline.class);
                    posts.add(post);
                }

                adapter = new TimelineAdapter(posts, getActivity());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void browseImage() {

        Intent galleryIntent=new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            mDialogue.setTitle(Utils.UPLOAD_PHOTO);
            mDialogue.setMessage(Utils.WAIT);
            mDialogue.show();
            prefs=PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Utils.COUPLE_KEYCODE,"");

            Uri resultUri = data.getData();
            StorageReference filePath=storageRef.child(Utils.CHILD_TIMELINE).child(prefs).child(code+".jpg");
            filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        mDialogue.hide();
                        downloadUrl=task.getResult().getDownloadUrl().toString();

                    }
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChatFragment.OnFragmentInteractionListener) {
            mListener = (ChatFragment.OnFragmentInteractionListener) context;
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
}
