package com.example.fromscratch;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class produits extends Fragment {

    private String name;
    private String key;
    private TextView marchandName;
    private Button button;
    private Button modifierProfil;
    private RecyclerView modifProduits;
    private FirebaseRecyclerOptions<produitPostsHelperClass> fireoption;
    private FirebaseRecyclerAdapter<produitPostsHelperClass,produitsModifierViewHolder> fireadapt;
    private FirebaseDatabase root;
    private DatabaseReference referenceProduit;
    private DatabaseReference referencePosition;
    FusedLocationProviderClient mFusedLocationClient;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.produit,container,false);

        button = v.findViewById(R.id.ajouterProduit);
        modifierProfil = v.findViewById(R.id.modifierProfil);
        marchandName = v.findViewById(R.id.dispName);
        modifProduits = v.findViewById(R.id.recyclerModifier);
        modifProduits.setLayoutManager(new LinearLayoutManager(getContext()));
        modifProduits.setHasFixedSize(true);
        root = FirebaseDatabase.getInstance();


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));


        if (getArguments() != null)
        {
            name = getArguments().getString("name");
            marchandName.setText(name);
        }

        if(name != null)
        {
            referenceProduit = root.getReference("Produits").child(name);
        }




        modifierProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), profilModif.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                referenceProduit = root.getReference("Produits").child(name);
                referencePosition = root.getReference("Marchand").child(name);
                referencePosition.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        MarchandHelperClass march = dataSnapshot.getValue(MarchandHelperClass.class);

                        if(march.getLatitude() == 0){
                            AlertDialog alertDialog =  new AlertDialog.Builder(getContext())
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("La position de votre magasin n'est pas définit!")
                                    .setMessage("definir la position actuel comme position du magasin?")
                                    .setPositiveButton("Oui", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            if (checkPermissions()) {
                                                if (isLocationEnabled()) {
                                                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                                                                        Toast.makeText(getContext(), "ERROR", Toast.LENGTH_LONG).show();
                                                                        requestNewLocationData();
                                                                    } else {

                                                                        referencePosition.child("longitude").setValue(location.getLongitude());
                                                                        referencePosition.child("latitude").setValue(location.getLatitude());
                                                                        Intent intent = new Intent(getContext() , produitPosts.class);
                                                                        intent.putExtra("name",name);
                                                                        startActivity(intent);

                                                                    }
                                                                }
                                                            }
                                                    );
                                                } else {
                                                    //Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                                    startActivity(intent);
                                                }
                                            } else {
                                                requestPermissions();
                                            }
                                        }

                                    })
                                    .setNegativeButton("Non", null)
                                    .show();
                        }
                        else
                        {
                            Intent intent = new Intent(getContext() , produitPosts.class);
                            intent.putExtra("name",name);
                            startActivity(intent);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        fireoption = new FirebaseRecyclerOptions.Builder<produitPostsHelperClass>().setQuery(referenceProduit,produitPostsHelperClass.class).build();
        fireadapt = new FirebaseRecyclerAdapter<produitPostsHelperClass, produitsModifierViewHolder>(fireoption) {
            @Override
            protected void onBindViewHolder(@NonNull final produitsModifierViewHolder holder, final int position, @NonNull final produitPostsHelperClass model) {
                holder.modifType.setText(model.getType());
                holder.modifMarque.setText(model.getMarque());
                holder.modifQuant.setText(model.getQuant());
                holder.modifJours.setText(model.getJour());
                holder.modifPrix.setText(model.getPrix()+getString(R.string.money));

                holder.supprimer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog alertDialog =  new AlertDialog.Builder(getContext())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Supprimer Produit")
                                .setMessage("etes vous sur de vouloir supprimer ce produit?")
                                .setPositiveButton("Oui", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        key = getRef(holder.getAdapterPosition()).getKey();
                                        referenceProduit.child(key).removeValue();
                                        Query noProducts = referenceProduit.child(name).orderByChild("type");
                                        noProducts.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(!dataSnapshot.exists())
                                                {
                                                    /*Fragment frg = null;
                                                    frg = getActivity().getSupportFragmentManager().findFragmentByTag("1");
                                                    final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                                    ft.detach(frg);
                                                    ft.attach(frg);
                                                    ft.commit();*/
                                                    Log.e("DID IT EXECUTE","yes it did ");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }

                                })
                                .setNegativeButton("Non", null)
                                .show();

                    }
                });
                holder.modifier.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String l = getRef(holder.getAdapterPosition()).getKey();
                        Toast.makeText(getContext(),l,Toast.LENGTH_LONG).show();
                        Intent intentModif = new Intent(getContext() , produitPosts.class);
                        intentModif.putExtra("productKey",getRef(holder.getAdapterPosition()).getKey());
                        intentModif.putExtra("name",name);
                        intentModif.putExtra("marque",model.getMarque());
                        intentModif.putExtra("type",model.getType());
                        intentModif.putExtra("jour",model.getJour());
                        intentModif.putExtra("quant",model.getQuant());
                        intentModif.putExtra("prix",model.getPrix());
                        startActivity(intentModif);
                    }
                });
            }

            @NonNull
            @Override
            public produitsModifierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.produit_modifier_view,parent,false);
                return new produitsModifierViewHolder(view);
            }
        };


        //loadData();

        fireadapt.startListening();
        modifProduits.setAdapter(fireadapt);

        return v;
    }


   /* private void loadData() {
    }*/


    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                (Activity) getContext(),
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
            Toast.makeText(getContext(),"CALL BACK: "+ mLastLocation.getLatitude() + " + " + mLastLocation.getLongitude() ,Toast.LENGTH_LONG).show();
        }
    };

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){

    }



    }



