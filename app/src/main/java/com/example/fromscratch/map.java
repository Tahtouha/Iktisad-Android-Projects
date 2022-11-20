package com.example.fromscratch;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.text.LocalGlyphRasterizer;

import java.util.ArrayList;
import java.util.Objects;

public class map extends Fragment {

    private FirebaseRecyclerOptions<MarchandHelperClass> options;
    private FirebaseRecyclerAdapter<MarchandHelperClass,marchandViewHolder> adapter;
    private RecyclerView listeMarchand;
    private FirebaseDatabase rootNode;
    private DatabaseReference referenceMarchand;
    private DatabaseReference referenceProduit;
    private SearchView search;
    private FloatingActionButton logOut;
    private String name;
    private marchandsAdapter.RecyclerClickListner mlistner;
    private String id;

    final ArrayList<String> names = new ArrayList<>();
    final ArrayList<String> searched = new ArrayList<>();
    final ArrayList<String> marchandsActif = new ArrayList<>();




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View listView =  inflater.inflate(R.layout.list,container,false);
        listeMarchand = listView.findViewById(R.id.ListeMarchand);
        search = listView.findViewById(R.id.search);
        logOut = listView.findViewById(R.id.logOut);
        listeMarchand.setHasFixedSize(true);
        listeMarchand.setLayoutManager(new LinearLayoutManager(getContext()));
        rootNode = FirebaseDatabase.getInstance();
        referenceMarchand = rootNode.getReference("Marchand");
        referenceProduit = rootNode.getReference("Produits");

        if(getArguments() != null)
        {
            id = getArguments().getString("ID");
        }

        return listView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //*****************************LOG OUT CODE

       logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               AlertDialog alertDialog =  new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Déconnection")
                        .setMessage("Êtes-vous sur de vouloir vous déconnecter?")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                SharedPreferences sp = getContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = sp.edit();
                                edit.clear();
                                edit.commit();

                                Intent intent = new Intent(getContext(), LogIn.class);
                                startActivity(intent);
                                getActivity().finish();

                            }

                        })
                        .setNegativeButton("Non", null)
                        .show();


            }
        });

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
                marchandsActif.clear();

                for (final String counter : names) {

                    Query checkType = referenceProduit.child(counter).orderByChild("type");


                    checkType.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                if(ds.exists())
                                {
                                    marchandsActif.add(counter);
                                    break;

                                }

                            }

                             setAdapter(marchandsActif);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });







        if (getArguments() != null)
        {
            name = getArguments().getString("name");
        }


        //setAdapter(names);
/*        final ArrayList<MarchandHelperClass> namesList = new ArrayList<>();

        options = new FirebaseRecyclerOptions.Builder<MarchandHelperClass>().setQuery(referenceMarchand,MarchandHelperClass.class).build();
        adapter = new FirebaseRecyclerAdapter<MarchandHelperClass, marchandViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final marchandViewHolder holder, final int position, @NonNull MarchandHelperClass model) {

                    holder.nameMarchand.setText(model.getUsername());
                    holder.phone.setText(model.getTelephone());

                    referenceMarchand.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            namesList.clear();
                            for(DataSnapshot postSnapshot: dataSnapshot.getChildren())
                            {
                                MarchandHelperClass objetMarchand = postSnapshot.getValue(MarchandHelperClass.class);
                                namesList.add(objetMarchand);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                final ArrayList<produitPostsHelperClass> produitList = new ArrayList<>();
                referenceProduit.child(model.getUsername()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        produitList.clear();
                        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                            produitPostsHelperClass prod = postSnapshot.getValue(produitPostsHelperClass.class);
                            produitList.add(prod);
                            // here you can access to name property like prod.name


                            for(int i = 0; i < produitList.size(); i++) {
                                if(i == 0)
                                {
                                    holder.itemA.setText(produitList.get(i).getType());
                                }
                                 if(i == 1)
                                {
                                    holder.itemB.setText(produitList.get(i).getType());
                                }
                                 if(i == 2)
                                {
                                    holder.itemC.setText(produitList.get(i).getType());
                                }

                                if(i > 2)
                                {
                                    produitList.clear();
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                    holder.v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(),magasinProfil.class);
                            intent.putExtra("nomMagasin",getRef(position).getKey());
                            intent.putExtra("nomMag",name);
                            startActivity(intent);
                        }
                    });


            }

            @NonNull
            @Override
            public marchandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_magasin,parent,false);
                return new marchandViewHolder(view);
            }
        };

        adapter.startListening();
        listeMarchand.setAdapter(adapter);
*/



        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {
                //Query checkType = referenceProduit.child("Taha").orderByChild("type");

                if (!s.isEmpty()) {


                    for (final String counter : names) {

                        Query checkType = referenceProduit.child(counter).orderByChild("type")/*.startAt(s).endAt(s+"\uf8ff");*/;

                        checkType.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    if (ds.child("type").getValue().toString().contains(s)) {
                                        Log.e("Type", counter);
                                        searched.add(counter);

                                    }


                                }

                                setAdapter(searched);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }



                    searched.clear();

                }

                else
                {
                    marchandsActif.clear();

                    for (final String counter : names) {

                        Query checkType = referenceProduit.child(counter).orderByChild("type");


                        checkType.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                    if(ds.exists())
                                    {
                                        marchandsActif.add(counter);
                                        Log.e("Type",counter);
                                        break;

                                    }

                                }

                                setAdapter(marchandsActif);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });
                    }

                    /*options = new FirebaseRecyclerOptions.Builder<MarchandHelperClass>().setQuery(referenceMarchand,MarchandHelperClass.class).build();
                    adapter = new FirebaseRecyclerAdapter<MarchandHelperClass, marchandViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull final marchandViewHolder holder, final int position, @NonNull MarchandHelperClass model) {
                            holder.nameMarchand.setText(model.getUsername());
                            holder.phone.setText(model.getTelephone());


                            final ArrayList<produitPostsHelperClass> produitList = new ArrayList<>();
                            referenceProduit.child(model.getUsername()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    produitList.clear();
                                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                                        produitPostsHelperClass prod = postSnapshot.getValue(produitPostsHelperClass.class);
                                        produitList.add(prod);
                                        // here you can access to name property like prod.name


                                        for(int i = 0; i < produitList.size(); i++) {
                                            if(i == 0)
                                            {
                                                holder.itemA.setText(produitList.get(i).getType());
                                            }
                                            if(i == 1)
                                            {
                                                holder.itemB.setText(produitList.get(i).getType());
                                            }
                                            if(i == 2)
                                            {
                                                holder.itemC.setText(produitList.get(i).getType());
                                            }

                                            if(i > 2)
                                            {
                                                produitList.clear();
                                            }

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });



                            holder.v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getContext(),magasinProfil.class);
                                    intent.putExtra("nomMagasin",getRef(position).getKey());
                                    intent.putExtra("nomMag",name);
                                    startActivity(intent);
                                }
                            });
                        }

                        @NonNull
                        @Override
                        public marchandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_magasin,parent,false);
                            return new marchandViewHolder(view);
                        }
                    };

                    adapter.startListening();
                    listeMarchand.setAdapter(adapter);*/
                }


                return true;
            }
        });

    }

    private void setAdapter(ArrayList<String> search){
        setOnClickListner(search);
        marchandsAdapter maradapter = new marchandsAdapter(getContext(),search,name,mlistner);
        listeMarchand.setAdapter(maradapter);
        listeMarchand.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void setOnClickListner(final ArrayList<String> search) {
        mlistner = new marchandsAdapter.RecyclerClickListner() {
            @Override
            public void onClick(View v, int position) {
                if(id != null )
                {
                    Intent intent = new Intent(getContext(),magasinProfil.class);
                    intent.putExtra("nomMagasin",search.get(position));
                    intent.putExtra("nomMag",name);
                    intent.putExtra("id",id);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(getContext(),magasinProfil.class);
                    intent.putExtra("nomMagasin",search.get(position));
                    intent.putExtra("nomMag",name);
                    startActivity(intent);
                }

            }
        };
    }

}