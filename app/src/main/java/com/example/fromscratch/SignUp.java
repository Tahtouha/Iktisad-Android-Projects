package com.example.fromscratch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    private static final Pattern PHONE_NUMBER = Pattern.compile(("0[0-9]{9}"));

    FirebaseDatabase rootNode;
    DatabaseReference referenceUtilisateur;
    DatabaseReference referenceMarchand;

    EditText userName;
    EditText Password;
    EditText verfierPassword;
    EditText storName;
    EditText adress;
    EditText mail;
    EditText telephone;
    EditText registre;
    CheckBox checkBox;
    LinearLayout linearLayout;
    Button register;
    UtilisateurHelperClass user;
    MarchandHelperClass marchand;
    ArrayList<Integer> codeRegistre = new ArrayList<Integer>();
    FusedLocationProviderClient mFusedLocationClient;
    private ArrayList<String> names = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Declarations
        linearLayout = findViewById(R.id.linlay);
        checkBox = findViewById(R.id.userType);
        userName = findViewById(R.id.username);
        Password = findViewById(R.id.password);
        verfierPassword = findViewById(R.id.verfierPassword);
        register = findViewById(R.id.register);
        storName = new EditText(this);
        adress = new EditText(this);
        telephone = new EditText(this);
        mail = new EditText(this);
        registre = new EditText(this);
        rootNode = FirebaseDatabase.getInstance();
        referenceUtilisateur = rootNode.getReference("Utilisateur");
        referenceMarchand = rootNode.getReference("Marchand");
        user = new UtilisateurHelperClass();
        marchand = new MarchandHelperClass();

        codeRegistre.add(301113);
        codeRegistre.add(301114);
        codeRegistre.add(402001);
        codeRegistre.add(402002);
        codeRegistre.add(402115);
        codeRegistre.add(501101);
        codeRegistre.add(501111);
        codeRegistre.add(501112);
        codeRegistre.add(511101);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL

        );



        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                referenceMarchand.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            names.clear();
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                            {
                                MarchandHelperClass mar = dataSnapshot1.getValue(MarchandHelperClass.class);
                                names.add(mar.getUsername());
                            }

                        }

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }


                });

                if (b) {
                    lp.setMargins(150, 0, 150, 150);
                    storName.setLayoutParams(lp);

                    telephone.setLayoutParams(lp);
                    telephone.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    telephone.setWidth(200);
                    telephone.setBackground(getDrawable(R.drawable.custom_edittxt));
                    telephone.setHint("Numero téléphone");
                    telephone.setInputType(InputType.TYPE_CLASS_NUMBER);

                    registre.setLayoutParams(lp);
                    registre.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    registre.setWidth(200);
                    registre.setBackground(getDrawable(R.drawable.custom_edittxt));
                    registre.setHint("Numero du registre");
                    registre.setInputType(InputType.TYPE_CLASS_NUMBER);

                    mail.setLayoutParams(lp);
                    mail.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    mail.setWidth(200);
                    mail.setBackground(getDrawable(R.drawable.custom_edittxt));
                    mail.setHint("Adress Mail");
                    mail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                    adress.setLayoutParams(lp);
                    adress.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    adress.setWidth(200);
                    adress.setBackground(getDrawable(R.drawable.custom_edittxt));
                    adress.setHint("Adress du magasin");



                    linearLayout.addView(telephone);
                    linearLayout.addView(registre);
                    linearLayout.addView(adress);
                    linearLayout.addView(mail);
                } else {
                    linearLayout.removeView(registre);
                    linearLayout.removeView(telephone);
                    linearLayout.removeView(adress);
                    linearLayout.removeView(mail);
                }

            }
        });

        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (checkBox.isChecked()) {
                    if(userName.getText().length() == 0 || Password.getText().length() == 0||adress.getText().length() == 0||mail.getText().length() == 0||telephone.getText().length() == 0||registre.getText().length() == 0)
                    {
                        Toast.makeText(SignUp.this,"Veuillez remplir toutes les cases",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        if(checkInputs())
                        {
                            addMarchad();
                        }

                    }

                }
                else
                {
                    if(checkInputs())
                    {

                        addUser();
                    }


                }



            }
        });


    }

    protected void getLocation() {


    }

    public void addMarchad() {

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                final Location location = task.getResult();
                                if (location == null) {
                                    Toast.makeText(SignUp.this, "ERROR", Toast.LENGTH_LONG).show();
                                    requestNewLocationData();
                                } else {
                                    if(mail.getText().length() != 0 || userName.getText().length() != 0 || telephone.getText().length() != 0 || adress.getText().length() != 0 || Password.getText().length() != 0 ){
                                        if(Password.getText().toString().equals(verfierPassword.getText().toString()))
                                            {



                                                if(names.contains(userName.getText().toString()))
                                                {
                                                    Toast.makeText(SignUp.this, "ce nom d'utilisateur est deja utilisé!", Toast.LENGTH_LONG).show();
                                                }
                                                else
                                                {
                                                    if(codeRegistre.contains(Integer.parseInt(registre.getText().toString())))
                                                    {
                                                        Toast.makeText(SignUp.this, "everyThing is in order", Toast.LENGTH_LONG).show();

                                                        AlertDialog alertDialog =  new AlertDialog.Builder(SignUp.this)
                                                                .setMessage("Voulez vous enregistrer votre position en tant que position du magasin?")
                                                                .setPositiveButton("Oui", new DialogInterface.OnClickListener()
                                                                {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        marchand.setLatitude(location.getLatitude());
                                                                        marchand.setLongitude(location.getLongitude());
                                                                        marchand.setBlockage(false);
                                                                        marchand.setMail(mail.getText().toString());
                                                                        marchand.setAdress(adress.getText().toString());
                                                                        marchand.setSignalement(0);
                                                                        marchand.setUsername(userName.getText().toString().trim());
                                                                        marchand.setTelephone(telephone.getText().toString().trim());
                                                                        marchand.setPassword(Password.getText().toString().trim());
                                                                        referenceMarchand.child(String.valueOf(userName.getText())).setValue(marchand);
                                                                        finish();

                                                                    }

                                                                })
                                                                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        marchand.setLatitude(0);
                                                                        marchand.setLongitude(0);
                                                                        marchand.setBlockage(false);
                                                                        marchand.setMail(mail.getText().toString());
                                                                        marchand.setAdress(adress.getText().toString());
                                                                        marchand.setSignalement(0);
                                                                        marchand.setUsername(userName.getText().toString().trim());
                                                                        marchand.setTelephone(telephone.getText().toString().trim());
                                                                        marchand.setPassword(Password.getText().toString().trim());
                                                                        referenceMarchand.child(String.valueOf(userName.getText())).setValue(marchand);
                                                                        finish();
                                                                    }
                                                                })
                                                                .show();
                                                    }
                                                    else{
                                                        Toast.makeText(SignUp.this, "LE code de registre de commerce est incorrect", Toast.LENGTH_LONG).show();
                                                    }


                                                }

                                            }
                                        else{
                                            Toast.makeText(SignUp.this, "mot de incorrect", Toast.LENGTH_LONG).show();

                                        }



                                    }
                                    else{
                                        Toast.makeText(SignUp.this, "veuiller remplir toutes les casses", Toast.LENGTH_LONG).show();
                                    }


                                   // Toast.makeText(SignUp.this, "Sabha: " + location.getLatitude() + " + " + location.getLongitude(), Toast.LENGTH_LONG).show();

                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }



    }

    public void addUser ()
    {
        if(userName.getText().length() == 0 || Password.getText().length() == 0)
        {
            Toast.makeText(SignUp.this,"dir swal7ek nichen",Toast.LENGTH_LONG).show();
        }
        else
        {
            if(Password.getText().toString().equals(verfierPassword.getText().toString()))
            {
                Toast.makeText(SignUp.this,"BDDBDDBDD",Toast.LENGTH_LONG).show();
                user.setUsername(userName.getText().toString().trim());
                user.setPassword(Password.getText().toString().trim());
                referenceUtilisateur.child(String.valueOf(userName.getText())).setValue(user);
                Intent intent = new Intent(SignUp.this, LogIn.class);
                startActivity(intent);

            }
            else
                Toast.makeText(SignUp.this,"mechi kifkif",Toast.LENGTH_LONG).show();

        }



    }

   /*public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return locationMode != Settings.Secure.LOCATION_MODE_OFF;


    }*/

    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},44);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Granted. Start getting the location information
            }
        }
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            /*latTextView.setText(mLastLocation.getLatitude()+"");
            lonTextView.setText(mLastLocation.getLongitude()+"");*/
            Toast.makeText(SignUp.this,"CALL BACK: "+ mLastLocation.getLatitude() + " + " + mLastLocation.getLongitude() ,Toast.LENGTH_LONG).show();
        }
    };

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){

    }

    private boolean checkInputs()
    {
        if(Password.getText().length()< 5)
        {
            Toast.makeText(SignUp.this,"Le mot de passe doit contenir 5 caractères au minimum",Toast.LENGTH_LONG).show();
            return false;
        }
        else
        { if (checkBox.isChecked())
        {
            if(!Patterns.EMAIL_ADDRESS.matcher(mail.getText()).matches())
            {
                Toast.makeText(SignUp.this,"L'adress mail que vous avez indiquer est invalide",Toast.LENGTH_LONG).show();
                return false;
            }

            else if (!PHONE_NUMBER.matcher(telephone.getText()).matches())
            {
                Toast.makeText(SignUp.this,"Le numero de téléphone que vous avez indiquer est invalide",Toast.LENGTH_LONG).show();
                return false;
            }
            else return true;
        }
        else return true;

        }

    }
}
