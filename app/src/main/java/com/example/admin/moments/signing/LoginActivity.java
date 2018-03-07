package com.example.admin.moments.signing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.preference.PreferenceManager;
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

import com.example.admin.moments.R;
import com.example.admin.moments.Utils;
import com.example.admin.moments.navigation.NavigationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText mEmail;
    private TextInputEditText mPassward;
    private Button mLogin;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialogue;
    private static final String TAG="LoginActivity";
    private Toolbar mToolBarLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mToolBarLog= findViewById(R.id.login_toolBar);
        setSupportActionBar(mToolBarLog);
        getSupportActionBar().setTitle(Utils.LOGIN);

        mDialogue = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.textEmailLogin);
        mPassward = findViewById(R.id.textPasswardLogin);
        mLogin = findViewById(R.id.buttonLogin);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mDisplayEmail = mEmail.getEditableText().toString();
                String mDisplayPassward = mPassward.getEditableText().toString();

            if( !TextUtils.isEmpty(mDisplayEmail) && !TextUtils.isEmpty(mDisplayPassward)){
                mDialogue.setTitle(Utils.LOGGING_MESSAGE);
                mDialogue.setMessage(Utils.WAIT);
                mDialogue.setCanceledOnTouchOutside(false);
                mDialogue.show();
                login(mDisplayEmail,mDisplayPassward);

            }
            else{
                Toast.makeText(LoginActivity.this, Utils.ENTER,
                        Toast.LENGTH_SHORT).show();
            }


            }
        });
    }
    private void login(String mDisplayEmail, String mDisplayPassward) {

            mAuth.signInWithEmailAndPassword(mDisplayEmail, mDisplayPassward)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mDialogue.dismiss();
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                if (PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).contains(Utils.COUPLE_KEYCODE)) {
                                    Intent logIntent=new Intent(LoginActivity.this,NavigationActivity.class);
                                    logIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(logIntent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, CheckCodeActivity.class);
                                    startActivity(intent);
                                   finish();
                                }


                            } else {
                                mDialogue.hide();
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, Utils.FAIL_AUTHENTICATION,
                                        Toast.LENGTH_SHORT).show();
                            }


                        }
                    });


    }
}
