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

import com.example.admin.moments.R;
import com.example.admin.moments.settings.SettingsActivity;
import com.example.admin.moments.signing.CheckCodeActivity;
import com.example.admin.moments.signing.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ChatFragment.OnFragmentInteractionListener,
        TimelineFragment.OnFragmentInteractionListener,
        CalendarFragment.OnFragmentInteractionListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private String id;
    public static String idChat1="";
    public static final String SAVE_USERID="save_userid";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Intent intent = getIntent();
        id=intent.getStringExtra(CheckCodeActivity.PARTNER_ID);
        getPreference(id);
        idChat1= PreferenceManager.getDefaultSharedPreferences(NavigationActivity.this).getString(SAVE_USERID,"");

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            mRef= FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //NOTE:  Checks first item in the navigation drawer initially
        navigationView.setCheckedItem(R.id.nav_chat);
        //NOTE:  Open fragment1 initially.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, new ChatFragment());
        ft.commit();
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
//        else{
//            mRef.child("online").setValue(true);
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
      //  mRef.child("online").setValue(false);

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
        Fragment fragment = null;
        if (id == R.id.nav_chat) {
            fragment = new ChatFragment();
        } else if (id == R.id.nav_timeline) {
            fragment = new TimelineFragment();
        } else if (id == R.id.nav_calendar) {
            fragment = new CalendarFragment();
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

    private void getPreference(String item){
        //save
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(NavigationActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SAVE_USERID,item);
        editor.apply();
    }
}
