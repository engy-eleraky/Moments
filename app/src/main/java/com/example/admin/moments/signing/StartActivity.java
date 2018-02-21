package com.example.admin.moments.signing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.admin.moments.MainActivity;
import com.example.admin.moments.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class StartActivity extends AppCompatActivity {
    private Button mButtonReg;
    private Button mButtonLogin;
    private SignInButton mButtonGoogleLog;
    private ViewPager mPager;
    private int [] layouts ={R.layout.first_slide,R.layout.second_slide,R.layout.third_slide};
    private MpagerAdapter mpagerAdapter;
    private LinearLayout mLayout;
    private ImageView[] dots;
    private static final int RC_SIGN_IN=1;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private static final String TAG="Main_Activity";
    private ProgressDialog mDialogue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mAuth = FirebaseAuth.getInstance();

        mPager=findViewById(R.id.pager);
        mpagerAdapter=new MpagerAdapter(layouts,this);
        mPager.setAdapter(mpagerAdapter);
        mLayout=findViewById(R.id.linear);
        mDialogue=new ProgressDialog(this);

                  ////////////////////////////
        //we need after that to go to main activity,but if it is the first time after installation
        //we need to go code activity and generate code,send invite or enter code then go to main activity

        mButtonReg=findViewById(R.id.buttonReg);
        mButtonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent=new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(registerIntent);
                finish();
            }
        });

        mButtonLogin=findViewById(R.id.buttonLog);
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent=new Intent(StartActivity.this,LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        mButtonGoogleLog=findViewById(R.id.buttonGoogleLog);
        mButtonGoogleLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogue.setMessage("please wait...");
                mDialogue.show();
                signIn();
            }
        });


        createDots(0);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                  createDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mDialogue.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Intent regIntent=new Intent(StartActivity.this,MainActivity.class);
                            startActivity(regIntent);
                            finish();
                        } else {
                            mDialogue.hide();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });

    }



    private void createDots(int currentPosition){
      if(mLayout!=null)
          mLayout.removeAllViews();
          dots = new ImageView[layouts.length];
      for(int i=0;i<layouts.length;i++)
      {
          dots[i]=new ImageView(this);
          if(i==currentPosition)
          {
            dots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.active_dots));
          }
          else{
              dots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.inactive_dots));

          }
       LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
               ViewGroup.LayoutParams.WRAP_CONTENT);
          params.setMargins(4,0,4,0);
          mLayout.addView(dots[i],params);
      }

    }
}

