package com.example.admin.moments.settings;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.admin.moments.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
    private Toolbar mToolBarStatus;
    private TextInputLayout mStatus;
    private Button mSave;
    private DatabaseReference mReference;
    private FirebaseUser mUser;
    private ProgressDialog mDialogue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mToolBarStatus= findViewById(R.id.status_toolBar);
        setSupportActionBar(mToolBarStatus);
        getSupportActionBar().setTitle("Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUser= FirebaseAuth.getInstance().getCurrentUser();
        String mUid=mUser.getUid();
        mReference= FirebaseDatabase.getInstance().getReference().child("couple").child("users").child(mUid);

         //get status from settings
        String statusValue=getIntent().getStringExtra("satus_value");
        mStatus=findViewById(R.id.textInputLayoutStatus);
        mStatus.getEditText().setText(statusValue);

        //save new status
        mSave=findViewById(R.id.buttonSave);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogue=new ProgressDialog(StatusActivity.this);
                mDialogue.setTitle("saving changes");
                mDialogue.setMessage("please wait");
                mDialogue.show();

                String status=mStatus.getEditText().getText().toString();
                mReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                     if (task.isSuccessful()) {
                         mDialogue.dismiss();

                     }else{

                         //handle errors
                     }

                 }
             });
            }
        });

    }
}
