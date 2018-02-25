package com.example.admin.moments.navigation;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.admin.moments.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends Fragment {
    private ChatFragment.OnFragmentInteractionListener mListener;


    public TimelineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_timeline, container, false);
        if (mListener != null) {
            mListener.onFragmentInteraction("Timeline");
        }
        return view;
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
        void onFragmentInteraction(String title );

    }
}
