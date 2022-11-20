package com.example.fromscratch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class produitPosts extends AppCompatActivity {

    Button poster;
    EditText type;
    EditText quant;
    EditText MarquePost;
    EditText jour;
    EditText prix;
    CheckBox don;
    String modifType;
    String modifMarque;
    String modifQuant;
    String modifJour;
    String modifPrix;
    produitPostsHelperClass produit;
    ArrayList<produitPostsHelperClass> produitList = new ArrayList<>();

    String name;

    FirebaseDatabase root;
    DatabaseReference products;
    DatabaseReference produitModifiable;
    DatabaseReference refMarchands;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produit_posts);
        poster = findViewById(R.id.poster);
        type = findViewById(R.id.type);
        MarquePost = findViewById(R.id.MarquePost);
        quant = findViewById(R.id.quant);
        jour =  findViewById(R.id.jour);
        prix = findViewById(R.id.prix);
        don = findViewById(R.id.don);
        root = FirebaseDatabase.getInstance();
        products = root.getReference("Produits");
        refMarchands = root.getReference("Marchand");

        if(getIntent().getStringExtra("productKey") != null)
        {
            String productKey = getIntent().getStringExtra("productKey");
            name = getIntent().getStringExtra("name");
            modifType = getIntent().getStringExtra("type");
            modifMarque = getIntent().getStringExtra("marque");
            modifQuant = getIntent().getStringExtra("quant");
            modifJour = getIntent().getStringExtra("jour");
            modifPrix = getIntent().getStringExtra("prix");

            type.setText(modifType);
            MarquePost.setText(modifMarque);
            quant.setText(modifQuant);
            jour.setText(modifJour);
            prix.setText(modifPrix);

            produitModifiable = root.getReference("Produits").child(name).child(productKey);
            ValueEventListener vel = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   /* produitPostsHelperClass helper = dataSnapshot.getValue(produitPostsHelperClass.class);
                    type.setText(helper.getType());
                    MarquePost.setText(helper.getMarque());
                    quant.setText(helper.getQuant());
                    jour.setText(helper.getJour());*/
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            produitModifiable.addValueEventListener(vel);
        }

        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ajouterProduit();
            }
        });


    }


    private void ajouterProduit()
    {
        name = getIntent().getStringExtra("name");
        String key = getIntent().getStringExtra("productKey");


        produit = new produitPostsHelperClass();
        produit.setJour(jour.getText().toString());
        produit.setQuant(quant.getText().toString());
        produit.setMarque(MarquePost.getText().toString());
        produit.setType(type.getText().toString());
        produit.setPrix(prix.getText().toString());
        if (key == null)
        {
            products.child(name).push().setValue(produit);
            /*refMarchands.child(name).child("produit").push().child("type").setValue(type.getText().toString());*/
        }
        else
        {
            products.child(name).child(key).setValue(produit);
    }
        Toast.makeText(produitPosts.this,"Produit posté avec succès ",Toast.LENGTH_LONG).show();
        finish();

    }

}
