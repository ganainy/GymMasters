package com.example.myapplication.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class ExcerciseAdapter extends RecyclerView.Adapter<ExcerciseAdapter.ExcerciseViewHolder> {


    @NonNull
    @Override
    public ExcerciseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ExcerciseViewHolder excerciseViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    //viewHolder
    public class ExcerciseViewHolder extends RecyclerView.ViewHolder{


        public ExcerciseViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }
}
