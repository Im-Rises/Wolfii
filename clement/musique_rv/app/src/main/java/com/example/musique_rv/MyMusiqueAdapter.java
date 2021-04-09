package com.example.musique_rv;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class MyVideoGamesAdapter extends RecyclerView.Adapter<MyVideoGamesAdapter.MyViewHolder> {
    List<Musique> mesMusiques;

    MyVideoGamesAdapter(List<Musique> mesMusiques) {
        this.mesMusiques = mesMusiques;
    }

    @NonNull
    @Override
    public MyVideoGamesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.musique_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVideoGamesAdapter.MyViewHolder holder, int position) {
        holder.display(mesMusiques.get(position));
    }

    @Override
    public int getItemCount() {
        return mesMusiques.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mNameTV;
        private TextView mPriceTV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mNameTV = (TextView) itemView.findViewById(R.id.name);
            mPriceTV = (TextView) itemView.findViewById(R.id.path);
        }

        void display(Musique jeuVideo) {
            mNameTV.setText(jeuVideo.getName());
            mPriceTV.setText(jeuVideo.getPath());
        }
    }
}
