package com.example.fromscratch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public class profilModif extends AppCompatActivity {

    TextInputLayout passwordModif;
    TextInputLayout adressModif;
    TextInputLayout mailModif;
    TextInputLayout numModif;

    Button EnregistrerModif;
    Button positionModif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_modif);

        passwordModif = findViewById(R.id.passwordModif);
        adressModif = findViewById(R.id.adressModif);
        mailModif = findViewById(R.id.mailModif);
        numModif = findViewById(R.id.numModif);
        //EnregistrerModif = findViewById(R.id.EnregistrerModif);
       // positionModif = findViewById(R.id.positionModif);

      /*  positionModif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SAVE CURRENT POSITION OR LOCATION PICKER
            }
        });*/

        /*EnregistrerModif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SAVE ALL INFO TO USER
            }
        });*/
    }
}