package com.example.admin.moments.signing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.admin.moments.MainActivity;
import com.example.admin.moments.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;

public class CheckMailingActivity extends AppCompatActivity {
    private TextInputEditText mText;
    private Button mButton;
    String textMail;
    private ProgressDialog mDialogue;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_mailing);

        mAuth = FirebaseAuth.getInstance();

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
                    //get two users id
                    //force second user go to main activity
                    Intent regIntent = new Intent(CheckMailingActivity.this, MainActivity.class);
                    startActivity(regIntent);
                    finish();
                }
            }
        });
    }
}
