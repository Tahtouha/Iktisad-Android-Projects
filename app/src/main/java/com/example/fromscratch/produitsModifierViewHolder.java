package com.example.fromscratch;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class produitsModifierViewHolder extends RecyclerView.ViewHolder {

    TextView modifType;
    TextView modifMarque;
    TextView modifQuant;
    TextView modifJours;
    TextView modifPrix;
    FloatingActionButton modifier;
    FloatingActionButton supprimer;

    public produitsModifierViewHolder(@NonNull View itemView) {
        super(itemView);
        modifType = itemView.findViewById(R.id.modifType);
        modifMarque = itemView.findViewById(R.id.modifMarque);
        modifQuant = itemView.findViewById(R.id.modifQuant);
        modifJours = itemView.findViewById(R.id.modifJours);
        modifPrix = itemView.findViewById(R.id.modifPrix);
        modifier = itemView.findViewById(R.id.modif);
        supprimer = itemView.findViewById(R.id.supp);

    }
}//hello                                                                                                                                                      