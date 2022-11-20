package com.example.fromscratch;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class produitViewHolder extends RecyclerView.ViewHolder {

    TextView type;
    TextView quantity;
    TextView joursRestants;
    TextView marqueProd;
    TextView prixProd;

    public produitViewHolder(@NonNull View itemView) {
        super(itemView);

        type = itemView.findViewById(R.id.typeProduit);
        quantity = itemView.findViewById(R.id.quantity);
        joursRestants = itemView.findViewById(R.id.joursRestants);
        marqueProd = itemView.findViewById(R.id.marqueProd);
        prixProd = itemView.findViewById(R.id.prixProd);

    }
}//hello                                                                                                                                                                                                                                                           