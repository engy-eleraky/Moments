package com.example.admin.moments.settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.moments.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private DatabaseReference mReference;
    private FirebaseUser mUser;
    private CircleImageView circleImageView;
    private TextView mName;
    private TextView mStatus;
    private Button mButtonStatus;
    private Button mButtonImage;
    private StorageReference mStorageRef;
    private ProgressDialog mDialogue;

    String mUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        circleImageView=findViewById(R.id.circleImageView);
        mName=findViewById(R.id.textViewName);
        mStatus=findViewById(R.id.textViewHi);
        mButtonStatus=findViewById(R.id.buttonStatus);
        mButtonImage=findViewById(R.id.buttonImage);

        mStorageRef = FirebaseStorage.getInstance().getReference();

                /*picasso*///////????????
   /*     Picasso.Builder builder=new Picasso.Builder(SettingsActivity.this);
        builder.downloader(new OkHttpDownloader(SettingsActivity.this,Integer.MAX_VALUE));
        Picasso built=builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);*/

        //when data changed,retreiving data
        mUser= FirebaseAuth.getInstance().getCurrentUser();
        mUid=mUser.getUid();
        mReference= FirebaseDatabase.getInstance().getReference().child("Users").child(mUid);
        //offline
        mReference.keepSynced(true);
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                final String image=dataSnapshot.child("image").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String thumb=dataSnapshot.child("thumbnail").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);

                ///offline
                Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.icon).into(circleImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {


                        Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.icon).into(circleImageView);
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mButtonStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String statusValue=mStatus.getText().toString();
                Intent statusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
                statusIntent.putExtra("satus_value",statusValue);
                startActivity(statusIntent);
            }
        });

        mButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 //start picker to get image for cropping and then use the image in cropping activity
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);
            }
        });


    }
              //uploading data
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mDialogue=new ProgressDialog(SettingsActivity.this);
                mDialogue.setTitle("uplaoding photo....");
                mDialogue.setMessage("please wait");
                mDialogue.setCanceledOnTouchOutside(false);
                mDialogue.show();
                           //upload photo
                Uri resultUri = result.getUri();
                //StorageReference filePath=mStorageRef.child("profile").child(random()+".jpg");
                StorageReference filePath=mStorageRef.child("profiles").child(mUid+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                                        //get url for image and set it in database
                            String downloadUrl=task.getResult().getDownloadUrl().toString();
                            mReference.child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mDialogue.hide();
                                    Toast.makeText(SettingsActivity.this,"working",Toast.LENGTH_LONG).show();

                                }
                            });
                        }else{
                            Toast.makeText(SettingsActivity.this," not working",Toast.LENGTH_LONG).show();
                            mDialogue.dismiss();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}
