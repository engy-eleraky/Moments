package com.example.admin.moments.navigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.admin.moments.R;
import com.example.admin.moments.Utils;
import com.example.admin.moments.models.MomentDate;
import com.example.admin.moments.settings.SettingsActivity;
import com.example.admin.moments.signing.CheckCodeActivity;
import com.example.admin.moments.signing.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ChatFragment.OnFragmentInteractionListener,
        TimelineFragment.OnFragmentInteractionListener,
        CalendarFragment.OnFragmentInteractionListener ,
        MediaFragment.OnFragmentInteractionListener,
        CalendarFragment.OnNewDateAddedListener


         {

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private NavigationView navigationView;
    private ShowListener calendarListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
         //NOTE:  Checks first item in the navigation drawer initially
        navigationView.setCheckedItem(R.id.nav_chat);

        View hView =  navigationView.getHeaderView(0);
        final TextView userText=hView.findViewById(R.id.userText);
        final TextView emailText=hView.findViewById(R.id.textView);
        final CircleImageView circleImageView=hView.findViewById(R.id.imageUser);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            mRef= FirebaseDatabase.getInstance().getReference().child(Utils.CHILD_USERS).child(mAuth.getCurrentUser().getUid());
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name=dataSnapshot.child(Utils.NAME).getValue().toString();
                    final String image=dataSnapshot.child(Utils.IMAGE).getValue().toString();
                    String email=dataSnapshot.child(Utils.EMAIL).getValue().toString();
                    userText.setText(name);
                    emailText.setText(email);

                    ///offline
                    Picasso.with(NavigationActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.icon).into(circleImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {


                            Picasso.with(NavigationActivity.this).load(image).placeholder(R.drawable.icon).into(circleImageView);
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


         if(savedInstanceState==null) {
             //NOTE:  Open fragment1 initially.
             FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
             ft.replace(R.id.mainFrame, new ChatFragment());
             ft.commit();
         }
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            Intent startIntent=new Intent(NavigationActivity.this,StartActivity.class);
            startActivity(startIntent);
            finish();
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.logoutId)
        {
            FirebaseAuth.getInstance().signOut();
            Intent startIntent=new Intent(NavigationActivity.this,StartActivity.class);
            startActivity(startIntent);
            finish();
        }
        if(item.getItemId()==R.id.settingsId)
        {
            Intent settingsIntent=new Intent(NavigationActivity.this,SettingsActivity.class);
            startActivity(settingsIntent);

        }


        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();
        navigationView.setCheckedItem(id);
        Fragment fragment = null;
        if (id == R.id.nav_chat) {
            fragment = new ChatFragment();
        } else if (id == R.id.nav_timeline) {
            fragment = new TimelineFragment();
        } else if (id == R.id.nav_new_date) {
            fragment = new CalendarFragment();
        }else if(id == R.id.nav_Media){
            fragment = new MediaFragment();
        }
        //NOTE: Fragment changing code
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();
        }
        //NOTE:  Closing the drawer after selecting
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(String title) {
        //NOTE:  Code to replace the toolbar title based current visible fragment
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onNewDateAdded(MomentDate momentDate) {

       navigationView.getMenu().add(R.id.calendar_group,momentDate.getId(),0,momentDate.title);

       Fragment fragment=new ChatFragment();

       FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
       ft.replace(R.id.mainFrame,fragment);
       ft.commit();

    }



}
