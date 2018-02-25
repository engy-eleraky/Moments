//package com.example.admin.moments.signing;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Color;
//import android.os.Build;
//import android.preference.PreferenceManager;
//import android.support.annotation.NonNull;
//import android.support.design.widget.TextInputLayout;
//import android.support.v4.app.ShareCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.admin.moments.MainActivity;
//import com.example.admin.moments.R;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.auth.ProviderQueryResult;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.nio.ByteBuffer;
//import java.util.UUID;
//
//public class CouplingActivity extends AppCompatActivity {
//    private TextView textCode;
//    private ImageButton share;
//    private TextInputLayout input;
//    private ImageButton done;
//    String item;
//    private DatabaseReference mCoupling;
//    private DatabaseReference mReference;
//    private FirebaseUser mCurrentUser;
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_coupling);
//        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
//       // mReference= FirebaseDatabase.getInstance().getReference().child("couple").child("users").child(mCurrentUser.getUid());
//        mReference=FirebaseDatabase.getInstance().getReference().child("couple").child("couples");
//        mAuth = FirebaseAuth.getInstance();
//
//
////        if (BuilmReference=d.VERSION.SDK_INT >= 21) {
////            getWindow().setStatusBarColor(Color.TRANSPARENT);
////
////        }
//
//        textCode=findViewById(R.id.textCode);
//        share=findViewById(R.id.buttonShare);
//        input=findViewById(R.id.textInputLayoutEnter);
//        done=findViewById(R.id.buttonDone);
//
////
////        enter();
////
////
////
////        //compare
////        done.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////
////
////            }
////        });
////
////        share.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                //share code
////                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(CouplingActivity.this)
////                        .setType("text/plain")
////                        .setText(item)
////                        .getIntent(), "share"));
////            }
//    //    });
//    }//on create
//
////private void enter(){
////
////    //generate,show
////    UUID uuid = UUID.randomUUID();
////    long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
////    item= Long.toString(l, Character.MAX_RADIX);
////    textCode.setText(item);
////
////    //save
////    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CouplingActivity.this);
////    SharedPreferences.Editor editor = preferences.edit();
////    editor.putString("match_code",item);
////    editor.apply();
////}
//}
