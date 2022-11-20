package com.example.fromscratch;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class marchandsAdapter extends RecyclerView.Adapter<marchandsAdapter.MyviewHolder> {

    Context context;
    ArrayList<String> data1;
    private RecyclerClickListner listner;
    private String user;
    private FirebaseDatabase root= FirebaseDatabase.getInstance();
    private DatabaseReference refMarchand = root.getReference("Marchand");
    private DatabaseReference refProduit = root.getReference("Produits");

    public marchandsAdapter(Context ct, ArrayList<String> usernames, String name,RecyclerClickListner listen)
    {
        context = ct;
        data1 = usernames;
        user = name;
        listner = listen;
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View v = inflater.inflate(R.layout.list_magasin,parent,false);

        return new MyviewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyviewHolder holder, final int position) {
        holder.nameMarchand.setText(data1.get(position));
        Query number =  refMarchand.child(data1.get(position)).child("telephone");
        number.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    holder.phone.setText(dataSnapshot.getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final ArrayList<produitPostsHelperClass> produitList = new ArrayList<>();
        refProduit.child(data1.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                produitList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    produitPostsHelperClass prod = postSnapshot.getValue(produitPostsHelperClass.class);
                    produitList.add(prod);
                    // here you can access to name property like prod.name


                    for (int i = 0; i < produitList.size(); i++) {
                        if (i == 0) {
                            holder.itemA.setText(produitList.get(i).getType());
                        }
                        if (i == 1) {
                            holder.itemB.setText(produitList.get(i).getType());
                        }
                        if (i == 2) {
                            holder.itemC.setText(produitList.get(i).getType());
                        }

                        if (i > 2) {
                            produitList.clear();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


      /*  holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(marchandsAdapter.class, magasinProfil.class);
                intent.putExtra("nomMagasin", data1.indexOf(position));
                intent.putExtra("nomMag", user);
                startActivity(intent);
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return data1.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        LinearLayout itemLayout;
        TextView nameMarchand;
        TextView phone;
        TextView itemA;
        TextView itemB;
        TextView itemC;
        View v;

        public MyviewHolder(@NonNull View itemView) {
            super(itemView);
            nameMarchand = itemView.findViewById(R.id.magasinName);
            phone = itemView.findViewById(R.id.phone);
            itemA = itemView.findViewById(R.id.item1);
            itemB = itemView.findViewById(R.id.item2);
            itemC = itemView.findViewById(R.id.item3);
            itemLayout = itemView.findViewById(R.id.itemMarchand);
            itemView.setOnClickListener(this);

            v = itemView;
        }

        @Override
        public void onClick(View view) {
            listner.onClick(view,getAdapterPosition());
        }
    }


    public interface RecyclerClickListner{

        void onClick(View v, int position);

    }

}
