package com.example.admin.moments.signing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.moments.R;
import com.example.admin.moments.Utils;
import com.example.admin.moments.models.Coding;
import com.example.admin.moments.models.OnWait;
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
import java.util.Random;
import java.util.UUID;

public class CheckCodeActivity extends AppCompatActivity {
    private EditText codeEditText;
    private TextView codeTextView;
    private Button codeButton;
    private ImageButton shareButton;
    private String code = "";
    private ProgressDialog mDialogue;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mReference;
    public static final String TAG = "CheckCodeActivity";
    public static final String CODE_KEY = "match_code";
    public static final String PARTNER_ID="partner_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_code);

        mAuth = FirebaseAuth.getInstance();
        mReference =FirebaseDatabase.getInstance().getReference();
        mCurrentUser = mAuth.getCurrentUser();

        mDialogue = new ProgressDialog(this);

        codeButton = findViewById(R.id.enterCodeButton);
        shareButton = findViewById(R.id.shareButton);
        codeEditText = findViewById(R.id.codeEditText);
        codeTextView = findViewById(R.id.codeTextView);


        // check if the partner matched the code
        mReference.child(Utils.CHILD_ONWAIT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(mCurrentUser.getUid())) {

                    // check if partner confirmed being a true partner.
                    mReference.child(Utils.CHILD_ONWAIT).child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            OnWait user = dataSnapshot.getValue(OnWait.class);

                            if (user != null && !user.id.equals(mCurrentUser.getUid())) {
                                //save
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CheckCodeActivity.this);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(Utils.COUPLE_KEYCODE, user.id);
                                editor.apply();
                                mReference.child(Utils.CHILD_ONWAIT).child(mCurrentUser.getUid()).removeValue();
                                Intent intent = new Intent(CheckCodeActivity.this, NavigationActivity.class);
                                startActivity(intent);
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    generateNewCode();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        codeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String enteredCode = codeEditText.getEditableText().toString();
                if( !TextUtils.isEmpty(enteredCode) ) {
                    mDialogue.setTitle("Please wait");
                    mDialogue.setMessage("Connecting you with your partner...");
                    mDialogue.setCanceledOnTouchOutside(false);
                    mDialogue.show();
                    checkCode(enteredCode);
                }

            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //share code
                String pref= PreferenceManager.getDefaultSharedPreferences(
                        CheckCodeActivity.this).getString(CODE_KEY,"");
                startActivity(Intent.createChooser(
                        ShareCompat.IntentBuilder.from(CheckCodeActivity.this)
                        .setType("text/plain")
                        .setText(pref)
                        .getIntent(), "share"));
            }
        });
    }
       // user enter code
    private void checkCode(final String code) {

        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> codes = dataSnapshot.child(Utils.CHILD_ONWAIT).getChildren().iterator();
                Coding coding;
                while (codes.hasNext()) {
                    coding = codes.next().getValue(Coding.class);
                    if (code.equals(coding.code)) {
                        mDialogue.dismiss();
                        Toast.makeText(CheckCodeActivity.this, "Welcome to chat with your partner :)", Toast.LENGTH_LONG).show();
                        String id = coding.id;

                        // add couple
                        createNewCouple(id);
                        Intent idIntent = new Intent(CheckCodeActivity.this, NavigationActivity.class);
                        startActivity(idIntent);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void createNewCouple(final String partnerId) {
        final DatabaseReference childCoupleRef = mReference.child(Utils.CHILD_COUPLES);
        childCoupleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long childrenCount = dataSnapshot.getChildrenCount();
                String coupleKeycode = String.valueOf(childrenCount + 1);
//                DatabaseReference coupleRef = childCoupleRef.child(coupleKeycode);

                //save
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CheckCodeActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(Utils.COUPLE_KEYCODE, coupleKeycode);
                editor.apply();

                // set partners values
                mReference.child(Utils.CHILD_COUPLES).child(coupleKeycode).child(Utils.PARTNER1_ID)
                        .setValue(mCurrentUser.getUid())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //handle errors
                            Toast.makeText(CheckCodeActivity.this,"id1 added",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(CheckCodeActivity.this,"id1 failed to added",Toast.LENGTH_LONG).show();

                        }
                    }
                });

                mReference.child(Utils.CHILD_COUPLES).child(coupleKeycode).child(Utils.PARTNER2_ID)
                        .setValue(partnerId)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //handle errors
                                    Toast.makeText(CheckCodeActivity.this,"id1 added",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(CheckCodeActivity.this,"id1 failed to added",Toast.LENGTH_LONG).show();

                                }
                            }
                        });

                // change onwait id to the couple keycode
                mReference.child(Utils.CHILD_ONWAIT).child(partnerId).child(Utils.ID)
                        .setValue(coupleKeycode)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //handle errors
                                    Toast.makeText(CheckCodeActivity.this,"id1 added",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(CheckCodeActivity.this,"id1 failed to added",Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                mReference.child(Utils.CHILD_ONWAIT).child(mCurrentUser.getUid()).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void generateNewCode(){

        //generate code
        Random random = new Random();
        for (int i = 0 ; i < 6 ; i++) {
            code += String.valueOf(random.nextInt(10));
        }
        codeTextView.post(new Runnable() {
            @Override
            public void run() {
                codeTextView.setText(code);
            }
        });

        //save
        /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CheckCodeActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CODE_KEY, code);
        editor.apply();
*/
        //create child

        mReference.child(Utils.CHILD_ONWAIT).child(mCurrentUser.getUid()).child("code").setValue(code).addOnCompleteListener(new OnCompleteListener<Void>() {
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

        mReference.child(Utils.CHILD_ONWAIT).child(mCurrentUser.getUid()).child(Utils.ID).setValue(mCurrentUser.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // watch for change in id field
                // if changed this means that the partner confirmed being a partner of this user
                mReference.child(Utils.CHILD_ONWAIT).child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        OnWait user = dataSnapshot.getValue(OnWait.class);
                        if (user != null && !user.id.equals(mCurrentUser.getUid())) {
                            mReference.removeEventListener(this);
                            //save
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CheckCodeActivity.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(Utils.COUPLE_KEYCODE, user.id);
                            editor.apply();
                            mReference.child(Utils.CHILD_ONWAIT).child(mCurrentUser.getUid()).removeValue();
                            Intent intent = new Intent(CheckCodeActivity.this, NavigationActivity.class);
                            startActivity(intent);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }, 5000);

    }//generate

}
