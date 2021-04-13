package com.example.musique_affichage_recycler_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyVideoGamesAdapter extends RecyclerView.Adapter<MyVideoGamesAdapter.MyViewHolder> {
    List<JeuVideo> mesJeux;

    MyVideoGamesAdapter(List<JeuVideo> mesJeux) {
        this.mesJeux = mesJeux;
    }

    @NonNull
    @Override
    public MyVideoGamesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.jeu_video_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVideoGamesAdapter.MyViewHolder holder, int position) {
        holder.display(mesJeux.get(position));
    }

    @Override
    public int getItemCount() {
        return mesJeux.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mNameTV;
        private TextView mPriceTV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mNameTV = (TextView) itemView.findViewById(R.id.name);
            mPriceTV = (TextView) itemView.findViewById(R.id.price);
        }

        void display(JeuVideo jeuVideo) {
            mNameTV.setText(jeuVideo.getName());
            mPriceTV.setText(jeuVideo.getPrice() + "€");
        }
    }
}
