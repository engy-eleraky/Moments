package com.example.admin.moments.signing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.admin.moments.MainActivity;
import com.example.admin.moments.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText mName;
    private TextInputEditText mEmail;
    private TextInputEditText mPassward;
    private Button mCreate;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialogue;
    private Toolbar mToolBar;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private static final String TAG="RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mToolBar= findViewById(R.id.register_toolBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Register");

        mDialogue=new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        mName=findViewById(R.id.textName);
        mEmail=findViewById(R.id.textEmail);
        mPassward=findViewById(R.id.textPassward);
        mCreate=findViewById(R.id.button3);
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mDisplayName=mName.getEditableText().toString();
                String mDisplayEmail=mEmail.getEditableText().toString();
                String mDisplayPassward=mPassward.getEditableText().toString();

                if(!TextUtils.isEmpty(mDisplayName) && !TextUtils.isEmpty(mDisplayEmail) && !TextUtils.isEmpty(mDisplayPassward)){
                    mDialogue.setTitle("Registering user");
                    mDialogue.setMessage("please wait");
                    mDialogue.setCanceledOnTouchOutside(false);
                    mDialogue.show();

                    registerUser(mDisplayName,mDisplayEmail,mDisplayPassward);

                }
                else{
                    Toast.makeText(RegisterActivity.this, "please enter your email/passward/name",
                            Toast.LENGTH_SHORT).show();
                }


            }
        });


    }
    //new user
    private void registerUser(final String mDisplayName, String mDisplayEmail, String mDisplayPassward) {
        mAuth.createUserWithEmailAndPassword(mDisplayEmail, mDisplayPassward)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid=user.getUid();
                            mReference=FirebaseDatabase.getInstance().getReference().child("couple").child("users").child(uid);
                            HashMap<String,String> map=new HashMap<>();
                            map.put("name",mDisplayName);
                            map.put("status","Hi .....");
                            map.put("image","default");
                            map.put("thumbnail","default");
                            mReference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mDialogue.dismiss();
                                        Intent regIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                        regIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(regIntent);
                                        finish();
                                    }
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            mDialogue.hide();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

}

