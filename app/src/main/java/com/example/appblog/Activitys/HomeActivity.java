package com.example.appblog.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appblog.Adapter.PostAdapter;
import com.example.appblog.Fragments.HomeFragment;
import com.example.appblog.Fragments.ProfileFragment;
import com.example.appblog.Fragments.SettingFragment;
import com.example.appblog.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private Toolbar mToobar;
    private ActionBarDrawerToggle mToggle;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private NavigationView mNav;
    private BottomNavigationView mBottom;
    private PostAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        init();
    }
    private void init() {
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        mDrawer=(DrawerLayout)findViewById(R.id.mDrawer);
        mToobar=(Toolbar)findViewById(R.id.home_Toolbar);
        setSupportActionBar(mToobar);
        mToggle=new ActionBarDrawerToggle(this,mDrawer,mToobar,R.string.app_name,R.string.app_close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        mNav=(NavigationView)findViewById(R.id.drawer_Nav);
        mNav.setNavigationItemSelectedListener(myNav);
        getSupportActionBar().setTitle("Home");

        getSupportFragmentManager().beginTransaction().replace(R.id.home_fram,new HomeFragment()).commit();
        mBottom=(BottomNavigationView)findViewById(R.id.mBottom);
        mBottom.setOnNavigationItemSelectedListener(myBottom);

       updateUserProfile();
    }
   private BottomNavigationView.OnNavigationItemSelectedListener myBottom=new BottomNavigationView.OnNavigationItemSelectedListener() {
       @Override
       public boolean onNavigationItemSelected(@NonNull MenuItem item) {
           switch (item.getItemId()){
               case R.id.nav_home:{
                   getSupportActionBar().setTitle("Home");
                   getSupportFragmentManager().beginTransaction().setCustomAnimations( R.anim.slide_right_in,
                           R.anim.slide_left_out,
                           R.anim.slide_left_in,
                           R.anim.slide_right_out).replace(R.id.home_fram,new HomeFragment()).commit();
                   break;
               }
               case R.id.nav_logout:{
                   mAuth.signOut();
                   Intent i=new Intent(HomeActivity.this,LoginActivity.class);
                   startActivity(i);
                   finish();
                   break;
               }
               case R.id.nav_profile:{
                   getSupportActionBar().setTitle("Profile");
                   getSupportFragmentManager().beginTransaction().setCustomAnimations( R.anim.slide_right_in,
                           R.anim.slide_left_out,
                           R.anim.slide_left_in,
                           R.anim.slide_right_out).replace(R.id.home_fram,new ProfileFragment()).commit();
                   break;
               }
           }
           return true;
       }
   };

    private void updateUserProfile() {
        View v=mNav.getHeaderView(0);
        ImageView header_img=(ImageView)v.findViewById(R.id.drawer_image);
        TextView header_name=(TextView)v.findViewById(R.id.drawer_name);
        TextView header_mail=(TextView)v.findViewById(R.id.drawer_mail);
        header_name.setText(currentUser.getDisplayName());
        header_mail.setText(currentUser.getEmail());
        Glide.with(HomeActivity.this).load(currentUser.getPhotoUrl()).into(header_img);
    }

    private NavigationView.OnNavigationItemSelectedListener myNav=new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.nav_logout:{
                    mAuth.signOut();
                    mDrawer.closeDrawers();
                    Intent LoginIntent=new Intent(HomeActivity.this,LoginActivity.class);
                    startActivity(LoginIntent);
                    finish();
                    break;
                }
            }
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}