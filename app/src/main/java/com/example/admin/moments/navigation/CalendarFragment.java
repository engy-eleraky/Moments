package com.example.admin.moments.navigation;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.moments.R;
import com.example.admin.moments.Utils;
import com.example.admin.moments.models.MomentDate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.okhttp.internal.Util;

import java.util.Calendar;

public class CalendarFragment extends Fragment implements DatePickerDialog.OnDateSetListener,
        AdapterView.OnItemSelectedListener{
    private ChatFragment.OnFragmentInteractionListener mListener;
    private OnNewDateAddedListener dateAddedListener;

    private EditText titleInput;
    private Button dateButton;
    private Spinner reminderSpinner;
    private LinearLayout cancelButton;
    private LinearLayout doneButton;

    private int selectedReminderOption;
    private String dateAsText;

    private DatabaseReference mRef;
    private String prefs="";

    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_calendar, container, false);
        if (mListener != null) {
            mListener.onFragmentInteraction(Utils.CHILD_CALENDAR);
        }

        mRef = FirebaseDatabase.getInstance().getReference();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(Utils.COUPLE_KEYCODE, "");

        reminderSpinner = view.findViewById(R.id.reminderSpinner);
        dateButton = view.findViewById(R.id.dateButton);
        titleInput = view.findViewById(R.id.titleInput);
        doneButton = view.findViewById(R.id.doneButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        final Calendar c = Calendar.getInstance();  // current date
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

        String separator = "/";
        dateAsText = String.valueOf(dayOfMonth) + separator + String.valueOf(month+1) + separator + String.valueOf(year);
        dateButton.setText(dateAsText);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.reminder_spinner_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        reminderSpinner.setAdapter(adapter);
        reminderSpinner.setOnItemSelectedListener(this);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleInput.getEditableText().toString();
                String date = (String) dateButton.getText();

                final MomentDate momentDate = new MomentDate(title, date, selectedReminderOption);

                mRef.child(Utils.CHILD_COUPLES).child(prefs).child(Utils.CHILD_CALENDAR).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long noOfChildren = dataSnapshot.getChildrenCount();
                        DatabaseReference dateRef= mRef.child(Utils.CHILD_COUPLES).child(prefs).child(Utils.CHILD_CALENDAR)
                                .child(String.valueOf(noOfChildren+1));

                        dateRef.child(Utils.MOMENT_DATE_TITLE).setValue(momentDate.title);
                        dateRef.child(Utils.MOMENT_DATE_DATE).setValue(momentDate.date);
                        dateRef.child(Utils.MOMENT_DATE_REMIND).setValue(momentDate.remind);

                        momentDate.setId((int) (noOfChildren+1));
                        dateAddedListener.onNewDateAdded(momentDate);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChatFragment.OnFragmentInteractionListener) {
            mListener = (ChatFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNewDateAddedListener");
        }

        if (context instanceof OnNewDateAddedListener) {
            dateAddedListener = (OnNewDateAddedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNewDateAddedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String separator = "/";
        dateAsText = String.valueOf(dayOfMonth) + separator + String.valueOf(month+1) + separator + String.valueOf(year);
        dateButton.setText(dateAsText);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedReminderOption = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);

    }

    public void showDatePickerDialog() {
       /* newFragment = new DatePickerDialogFragment();
        newFragment.show(getChildFragmentManager(), "datePicker");
        newFragment.onDismiss(this);*/

        final Calendar c = Calendar.getInstance();  // current date
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.show();
    }

    interface OnNewDateAddedListener {
        void onNewDateAdded(MomentDate momentDate);
    }
}
