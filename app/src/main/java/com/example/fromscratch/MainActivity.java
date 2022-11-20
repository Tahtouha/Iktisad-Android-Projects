package com.example.fromscratch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mapbox.mapboxsdk.geometry.LatLng;

public class MainActivity extends AppCompatActivity {

    //TO DO: le passage d'information pour l'abonement

    BottomNavigationView bottomNavigationView;
    public Button button;
    public String marchandName;
    private double latitude;
    private double longitude;

    public static int temp=0;
    final Fragment fragment11 = new location();
    final Fragment fragment22 = new map();
    final Fragment fragment33 = new produits();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (getIntent().getIntExtra("source",0) == 1)
        {
            latitude = getIntent().getDoubleExtra("latitude",0);
            longitude = getIntent().getDoubleExtra("longitude",0);
        }

        final Bundle args = new Bundle();
        marchandName = getIntent().getStringExtra("marchand");

        args.putString("name",marchandName);

        if (getIntent().getIntExtra("source",0) == 1)
        {
            args.putDouble("latitude",latitude);
            args.putDouble("longitude",longitude);
            fragment11.setArguments(args);
        }

        //Toast.makeText(MainActivity.this,"Bienvenue "+marchandName,Toast.LENGTH_LONG).show();

        fragment11.setArguments(args);
        fragment22.setArguments(args);
        fragment33.setArguments(args);
        fm.beginTransaction().add(R.id.container, fragment33, "3").hide(fragment33).commit();
        fm.beginTransaction().add(R.id.container, fragment22, "2").hide(fragment22).commit();
        fm.beginTransaction().add(R.id.container,fragment11, "1").commit();


        // getSupportFragmentManager().beginTransaction().replace(R.id.container,new location()).commit();



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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
                    case R.id.produits:
                        fm.beginTransaction().hide(active).show(fragment33).commit();
                        active = fragment33;
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
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                Toast.makeText(MainActivity.this, "Reselected", Toast.LENGTH_SHORT).show();

            }
        });


    }



}
