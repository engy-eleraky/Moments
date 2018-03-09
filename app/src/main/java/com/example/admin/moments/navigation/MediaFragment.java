package com.example.admin.moments.navigation;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.admin.moments.FirebaseService;
import com.example.admin.moments.Utils;
import com.example.admin.moments.adapters.MediaAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Iterator;


import com.example.admin.moments.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MediaFragment extends Fragment implements OnImageSelectedListener {

    RecyclerView recyclerView;
    MediaAdapter adapter;
    private MediaFragment.OnFragmentInteractionListener mListener;


    public static final String GET_MEDIA_ACTION = "com.noga.MEDIA_GET";

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Uri> photosUrls = intent.getParcelableArrayListExtra(Intent.EXTRA_RETURN_RESULT);
            adapter = new MediaAdapter(photosUrls, getActivity(), MediaFragment.this);
            recyclerView.setAdapter(adapter);
        }
    };

    public MediaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_media, container, false);
        if (mListener != null) {
            mListener.onFragmentInteraction(Utils.CHILD_MEDIA);
        }
        recyclerView = (RecyclerView) view.findViewById(R.id.media_recycler_view);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent getMediaIntent = new Intent(getActivity(), FirebaseService.class);
        getMediaIntent.setAction(FirebaseService.ACTION_GET_MEDIA);

        getActivity().registerReceiver(receiver, new IntentFilter(GET_MEDIA_ACTION));
        getActivity().startService(getMediaIntent);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onImageSelected(Uri uri) {


        Intent intent = new Intent(getActivity(), ImageViewActivity.class);
        intent.setData(uri);
        startActivity(intent);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MediaFragment.OnFragmentInteractionListener) {
            mListener = (MediaFragment.OnFragmentInteractionListener) context;
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
