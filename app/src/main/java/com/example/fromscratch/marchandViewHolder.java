package com.example.fromscratch;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class marchandViewHolder extends RecyclerView.ViewHolder{

  TextView nameMarchand;
  TextView phone;
  TextView itemA;
  TextView itemB;
  TextView itemC;
  View v;

  public marchandViewHolder(@NonNull View itemView) {
    super(itemView);
    nameMarchand = itemView.findViewById(R.id.magasinName);
    phone = itemView.findViewById(R.id.phone);
    itemA = itemView.findViewById(R.id.item1);
    itemB = itemView.findViewById(R.id.item2);
    itemC = itemView.findViewById(R.id.item3);

    v = itemView;
  }
}