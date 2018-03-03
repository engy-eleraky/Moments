package com.example.admin.moments.signing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.moments.models.Coding;
import com.example.admin.moments.R;
import com.example.admin.moments.navigation.NavigationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.UUID;

public class CheckCodeActivity extends AppCompatActivity {
    private TextInputEditText code;
    private TextView mUserCode;
    private ImageButton share;
    private ImageButton done;
    String item;
    private DatabaseReference mReference;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialogue;
      public static final String PARTNER_ID="partner_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_code);
        code = findViewById(R.id.enterCode);
        mUserCode = findViewById(R.id.code);
        share = findViewById(R.id.imageButton);
        done = findViewById(R.id.done);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();
        mDialogue = new ProgressDialog(this);

        enter();

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //share code
                String pref=PreferenceManager.getDefaultSharedPreferences(CheckCodeActivity.this).getString("match_code","");
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(CheckCodeActivity.this)
                        .setType("text/plain")
                        .setText(pref)
                        .getIntent(), "share"));
            }
        });

        //compare
        done.setOnClickListener(new View.OnClickListener() {
            //getinput
            @Override
            public void onClick(View view) {

                String textCode = code.getEditableText().toString();
                if (!TextUtils.isEmpty(textCode)) {
                    mDialogue.setTitle("Checking existing user");
                    mDialogue.setMessage("please wait");
                    mDialogue.setCanceledOnTouchOutside(false);
                    mDialogue.show();
                    checkCode(textCode);
                }
            }
        });

    }


    private void enter(){

        //generate code
        UUID uuid = UUID.randomUUID();
        long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        item= Long.toString(l, Character.MAX_RADIX);
        mUserCode.setText(item);

        //save
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CheckCodeActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("match_code",item);
        editor.apply();

        //create child
        mReference.child("OnWait").child(mCurrentUser.getUid()).child("code").setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //handle errors
                    Toast.makeText(CheckCodeActivity.this,"code added",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(CheckCodeActivity.this,"code failed to added",Toast.LENGTH_LONG).show();

                }
            }
        });
        mReference.child("OnWait").child(mCurrentUser.getUid()).child("id").setValue(mCurrentUser.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //handle errors
                    Toast.makeText(CheckCodeActivity.this,"id added",Toast.LENGTH_LONG).show();

                }else{
                    Toast.makeText(CheckCodeActivity.this,"id failed to added",Toast.LENGTH_LONG).show();

                }
            }
        });
    }//enter

    private void checkCode(final String code){

        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> codes=dataSnapshot.child("OnWait").getChildren().iterator();
                Coding coding = null;
                while (codes.hasNext()){
                     coding=codes.next().getValue(Coding.class);
                    if(code.equals(coding.code)){
                        mDialogue.dismiss();
                        break;
                    }
                }
                Toast.makeText(CheckCodeActivity.this,"Welcome to chat with your partner :)",Toast.LENGTH_LONG).show();
                String id= coding.id;
                Intent idIntent = new Intent(CheckCodeActivity.this, NavigationActivity.class);
                        idIntent.putExtra(PARTNER_ID,id);
                        startActivity(idIntent);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }//check



}
