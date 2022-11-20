package com.example.fromscratch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class activityUser extends AppCompatActivity {

    BottomNavigationView navigationView;
    final Fragment fragment11 = new location();
    final Fragment fragment22 = new map();
    private double latitude;
    private double longitude;

    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        navigationView = findViewById(R.id.bottom_navigation);

        if (getIntent().getIntExtra("source",0) == 1)
        {
            latitude = getIntent().getDoubleExtra("latitude",0);
            longitude = getIntent().getDoubleExtra("longitude",0);
        }


        final Bundle args = new Bundle();

        String username = getIntent().getStringExtra("nameuser");
        args.putString("nameUser",username);
        args.putString("ID","user");

        if (getIntent().getIntExtra("source",0) == 1)
        {
            args.putDouble("latitude",latitude);
            args.putDouble("longitude",longitude);
            fragment11.setArguments(args);
        }

        fragment11.setArguments(args);
        fragment22.setArguments(args);
        //last added args for subs
        fm.beginTransaction().add(R.id.container, fragment22, "2").hide(fragment22).commit();
        fm.beginTransaction().add(R.id.container,fragment11, "1").commit();

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                //Fragment fragment = null;

                switch (item.getItemId())
                {

                    case R.id.Map:
                        fm.beginTransaction().hide(active).show(fragment11).commit();
                        active = fragment11;
                        return true;
                    case R.id.list:
                        fm.beginTransaction().hide(active).show(fragment22).commit();
                        active = fragment22;
                        return true;

                    /*case R.id.Map:
                        fragment = new location();
                        break;
                    case R.id.list:
                        fragment = new map();
                        break;
                    case R.id.produits:
                        fragment = new produits();
                        break;*/
                }
                return false;
               /* fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();

                return true;*/
            }
        });
        navigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                Toast.makeText(activityUser.this, "Reselected", Toast.LENGTH_SHORT).show();

            }
        });



    }
}