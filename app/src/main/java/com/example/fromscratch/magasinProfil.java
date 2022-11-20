package com.example.fromscratch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class magasinProfil extends AppCompatActivity {

  TextView nomMagasin;
  TextView adress;
  RecyclerView listeProduits;
  Button itineraire;
  Button abonnement;
  FirebaseRecyclerOptions<produitPostsHelperClass> opt;
  FirebaseRecyclerAdapter<produitPostsHelperClass,produitViewHolder> adapt;
  FirebaseDatabase root;
  DatabaseReference referenceProduit;

  boolean exist = false;

  double lat;
  double longi;

  String CHANNEL_ID ="1";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_magasin_profil);

    createNotificationChannel();

    nomMagasin =findViewById(R.id.nomMagasin);
    adress = findViewById(R.id.adress);
    listeProduits = findViewById(R.id.listeProduits);
    itineraire = findViewById(R.id.itineraire);
    abonnement = findViewById(R.id.abonnement);
   // LinearLayoutManager layoutManager = new LinearLayoutManager(magasinProfil.this,LinearLayoutManager.HORIZONTAL,false);
    listeProduits.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    listeProduits.setHasFixedSize(true);

    root = FirebaseDatabase.getInstance();

    final String name = getIntent().getStringExtra("nomMagasin");
    final String user = getIntent().getStringExtra("nomMag");
    final String id = getIntent().getStringExtra("id");
    final DatabaseReference ref = root.getReference("Marchand");


    abonnement.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {


        // ****************verification d'abo

       Query listAbo = ref.child(user).child("abonnement").orderByChild("name");
        listAbo.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {


            if(dataSnapshot.exists())
            {

              for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
              {

               if(postSnapshot.child("name").getValue().toString().equals(name))
                {
                  exist = true;
                }
              }

              if(exist)
              {
                Toast.makeText(magasinProfil.this,"déja abonné",Toast.LENGTH_LONG).show();
              }
              else{
                ref.child(user).child("abonnement").push().child("name").setValue(name);
              }

            }
          }

          @Override
          public void onCancelled(DatabaseError error) {
            // Failed to read value
          }
        });


       //---------code de notification

 /*       NotificationManagerCompat notificationManager = NotificationManagerCompat.from(magasinProfil.this);

       NotificationCompat.Builder builder = new NotificationCompat.Builder(magasinProfil.this, CHANNEL_ID)
               .setSmallIcon(R.drawable.lock_icon)
                .setContentTitle("wee samaitkom")
                .setContentText("mehdi est un pd w billy pow chbab alih")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);



// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1654653, builder.build());*/

      }
    });

    ref.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        MarchandHelperClass help = dataSnapshot.child(name).getValue(MarchandHelperClass.class);
        lat = help.getLatitude();
        longi = help.getLongitude();

      }

      @Override
      public void onCancelled(DatabaseError error) {
        // Failed to read value
      }
    });

    Toast.makeText(magasinProfil.this,name,Toast.LENGTH_LONG).show();
    nomMagasin.setText(name);
    assert name != null;
    referenceProduit = root.getReference("Produits").child(name);

    Toast.makeText(magasinProfil.this,name,Toast.LENGTH_LONG).show();


    loadData();


    adapt.startListening();
    listeProduits.setAdapter(adapt);
    itineraire.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        if(id != null)
        {
          Intent i = new Intent(magasinProfil.this, activityUser.class);
          i.putExtra("marchand",getIntent().getStringExtra("nomMag"));
          i.putExtra("source",1);
          i.putExtra("latitude", lat);
          i.putExtra("longitude",longi);
          startActivity(i);
        }
        else
        {
          Intent i = new Intent(magasinProfil.this, MainActivity.class);
          i.putExtra("marchand",getIntent().getStringExtra("nomMag"));
          i.putExtra("source",1);
          i.putExtra("latitude", lat);
          i.putExtra("longitude",longi);
          startActivity(i);
        }


      }
    });

  }

  private void loadData() {

    opt = new FirebaseRecyclerOptions.Builder<produitPostsHelperClass>().setQuery(referenceProduit,produitPostsHelperClass.class).build();
    adapt = new FirebaseRecyclerAdapter<produitPostsHelperClass, produitViewHolder>(opt) {


      @Override
      protected void onBindViewHolder(@NonNull produitViewHolder holder, int position, @NonNull produitPostsHelperClass model) {
        holder.type.setText(model.getType());
        holder.quantity.setText(model.getQuant());
        holder.marqueProd.setText(model.getMarque());
        holder.joursRestants.setText(model.getJour());
        holder.prixProd.setText(model.getPrix()+getString(R.string.money));
      }

      @NonNull
      @Override
      public produitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.produit_list_view,parent,false);
        return new produitViewHolder(view);
      }
    };
  }

  private void createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = getString(R.string.channel_name);
      String description = "jhb";
      int importance = NotificationManager.IMPORTANCE_DEFAULT;
      NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
      channel.setDescription(description);
      // Register the channel with the system; you can't change the importance
      // or other notification behaviors after this
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }
  }

}



