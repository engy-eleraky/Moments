package com.example.admin.moments.signing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.admin.moments.R;
import com.example.admin.moments.User;
import com.example.admin.moments.navigation.NavigationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CheckMailingActivity extends AppCompatActivity {
    private TextInputEditText mText;
    private Button mButton;
    String textMail;
    private ProgressDialog mDialogue;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mRef;
    public static final String TAG="checkmailing_activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_mailing);

        mAuth = FirebaseAuth.getInstance();
        mRef=FirebaseDatabase.getInstance();

        mText=findViewById(R.id.enterMail);
        mButton=findViewById(R.id.save);
        mDialogue = new ProgressDialog(this);
        mText=(TextInputEditText) findViewById(R.id.enterMail);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textMail=mText.getEditableText().toString();
                if( !TextUtils.isEmpty(textMail) ) {
                    mDialogue.setTitle("Checking existing user");
                    mDialogue.setMessage("please wait");
                    mDialogue.setCanceledOnTouchOutside(false);
                    mDialogue.show();

                    check(textMail);



                }else{
                    //clear text
                    Toast.makeText(CheckMailingActivity.this, "please enter your partner email",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void check( String mDisplayEmail) {

        mAuth.fetchProvidersForEmail(mDisplayEmail).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                //found
                boolean check=!task.getResult().getProviders().isEmpty();
                if(!check){
                    mDialogue.hide();
                    Toast.makeText(CheckMailingActivity.this, "not found",
                            Toast.LENGTH_LONG).show();

                }else{
                    mDialogue.dismiss();
                    Toast.makeText(CheckMailingActivity.this, "already exists",
                            Toast.LENGTH_LONG).show();


//                    mRef.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                        String key= mRef.getReference().child("couple").child("users").push().getKey();
//                         String val=    dataSnapshot.getRef().child("couple").child("users").setValue(key).getResult().toString();
//
//                            Toast.makeText(CheckMailingActivity.this,dataSnapshot.getValue().toString() ,
//                                    Toast.LENGTH_LONG).show();
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });

                    //get two users id
                    //force second user go to main activity
                    Intent regIntent = new Intent(CheckMailingActivity.this, NavigationActivity.class);
                    startActivity(regIntent);
                    finish();
                }
            }
        });
    }


//private boolean checkMail(String mDisplayEmail,DataSnapshot dataSnapshot){
//    Log.d(TAG,"check if user exists"+ mDisplayEmail);
//
//    User user=new User();
//    for(DataSnapshot ds:dataSnapshot.getChildren()){
//    user.setEmail(ds.getValue(User.class).getEmail());
//     if(user.getEmail().equals(mDisplayEmail)){
//
//         return true;
//     }


//        Firebase users = myFirebaseRef.child("users");
//        users.orderByChild("email").equalTo("z@m.com").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Users user= dataSnapshot.getChildren().iterator().next().getValue(Users.class);
//                Log.d("User",user.getUserID());
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//                Log.d("User",firebaseError.getMessage() );
//            }
//        });
   // }
//return false;
//}

}
