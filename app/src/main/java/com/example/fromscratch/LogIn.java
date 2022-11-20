package com.example.fromscratch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LogIn extends AppCompatActivity {

    EditText username;
    EditText password;
    CheckBox marchandCheck;
    CheckBox resterCo;
    FirebaseDatabase root;
    DatabaseReference user;
    DatabaseReference marchand;
    Button login;
    Button signUp;
    SharedPreferences sp;
    SharedPreferences spRet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.logInButton);
        signUp = findViewById(R.id.singup);
        marchandCheck = findViewById(R.id.marchandCheck);
        resterCo = findViewById(R.id.resterCo);

        root = FirebaseDatabase.getInstance();
        user = root.getReference("Utilisateur");
        marchand = root.getReference("Marchand");
        findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
        sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        spRet = getApplicationContext().getSharedPreferences("userInfo",Context.MODE_PRIVATE);
       //Toast.makeText(LogIn.this,spRet.getString("nameStr",""),Toast.LENGTH_LONG).show();
        if(!spRet.getString("nameStr","").isEmpty())
        {
            if(spRet.getString("userType","").equals("marchand"))
            {
                Intent intent = new Intent(LogIn.this, MainActivity.class);
                intent.putExtra("marchand",spRet.getString("nameStr",""));
                startActivity(intent);
                finish();
            }
            else
            {
                Intent intent = new Intent(LogIn.this, activityUser.class);
                intent.putExtra("nameuser",spRet.getString("nameStr",""));
                startActivity(intent);
            }
        }

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogIn.this, SignUp.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(marchandCheck.isChecked())
                {
                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                    marchandLogIn();
                }
                else{
                    userLogIn();
                }

                
            }

            private void marchandLogIn() {
                final String marchadnameDB = username.getText().toString().trim();
                Query checkMarchand = marchand.orderByChild("username").equalTo(marchadnameDB);

                checkMarchand.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            String passwordDB = dataSnapshot.child(marchadnameDB).child("password").getValue(String.class);
                            if(password.getText().toString().equals(passwordDB))
                            {
                                SharedPreferences.Editor editor = sp.edit();
                                if(resterCo.isChecked()){
                                    editor.putString("nameStr", marchadnameDB);
                                    editor.putString("passwordStr",password.getText().toString().trim());
                                    editor.putString("userType", "marchand");
                                    editor.clear();
                                    editor.commit();
                                    Toast.makeText(LogIn.this,"checker is checked",Toast.LENGTH_LONG).show();

                                }


                                Intent intent = new Intent(LogIn.this, MainActivity.class);
                                intent.putExtra("marchand",marchadnameDB);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(LogIn.this,"fausses informations d'identification",Toast.LENGTH_LONG).show();
                                findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
                            }
                        }
                        else
                        {
                            Toast.makeText(LogIn.this,"fausses informations d'identification",Toast.LENGTH_LONG).show();
                            findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


            private void userLogIn() {

                final String usernameDB = username.getText().toString().trim();
                Query checkUser = user.orderByChild("username").equalTo(usernameDB);


                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            String passwordDB = dataSnapshot.child(usernameDB).child("password").getValue(String.class);
                            if(password.getText().toString().equals(passwordDB))
                            {
                                SharedPreferences.Editor editor = sp.edit();
                                if(resterCo.isChecked()){
                                    editor.putString("nameStr", usernameDB);
                                    editor.putString("passwordStr",password.getText().toString().trim());
                                    editor.putString("userType", "user");
                                    editor.commit();
                                }

                                Intent intent = new Intent(LogIn.this, activityUser.class);
                                intent.putExtra("nameuser",usernameDB);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(LogIn.this,"fausses informations d'identification",Toast.LENGTH_LONG).show();
                                findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
                            }
                        }

                        else {
                            Toast.makeText(LogIn.this, "fausses informations d'identification",Toast.LENGTH_LONG).show();
                            findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }



        });
    }



}
