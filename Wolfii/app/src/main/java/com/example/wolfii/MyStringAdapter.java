package com.example.wolfii;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.example.wolfii.MainActivity.mesAlbums;
import static com.example.wolfii.MainActivity.mesAlbumsImages;
import static com.example.wolfii.MainActivity.mesMusiques;

public class MyStringAdapter extends RecyclerView.Adapter<MyStringAdapter.MyViewHolder> {
    // classe qui est responsable de chaque cellule
    // responsable du recyclage des view
    // view holder = accelerer le rendu de la liste, il sera déclaré au sein de l'adapter
    private List<String> mesArtistes;
    public static Context context;
    private Boolean isPlaylist = false;
    private Boolean isGenre = false;
    private Boolean isAlbum = false;

    private int positionAlbum = 0;

    public MyStringAdapter (ArrayList<String> mesArtistes, Context sContext) {
        this.mesArtistes = mesArtistes;
    }

    // SETTER
    public void setIsPlaylist(Boolean isPlaylist) {this.isPlaylist = isPlaylist;}
    public void setIsGenre(Boolean isGenre) {this.isGenre = isGenre;}
    public void setIsAlbum(Boolean isAlbum) {this.isAlbum = isAlbum;}

    // GETTER
    public Object getItem(int position) {
        return mesArtistes.get(position);
    }

    public interface ArtisteItemClickListener {
        void onArtisteItemClick(View view, String artiste, int position);
        void onArtisteItemLongClick(View view, String artiste, int position);
    }
    private ArtisteItemClickListener mArtisteItemClickListener;

    public void setmArtisteItemClickListener(ArtisteItemClickListener mArtisteItemClickListener) {
        this.mArtisteItemClickListener = mArtisteItemClickListener;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // on cherche notre vue avec inflater
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // on va chercher notre layout
        View view = layoutInflater.inflate(R.layout.simple_item_list, parent, false);
        if(isGenre) view = layoutInflater.inflate(R.layout.genre_item, parent, false);
        if(isAlbum) view = layoutInflater.inflate(R.layout.album_item, parent, false);
        // on renvoie le viewholder
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // affiche les viewholder en donnant la position
        holder.display(mesArtistes.get(position));
        if(isAlbum) {
            holder.display (mesAlbums.get(position));
            holder.showImage (position);
        }
        Log.d("position", position + "");
        if(isPlaylist) {
            String playlist = mesArtistes.get (position);
            ClickOnHolder clickOnHolder = new ClickOnHolder ();
            clickOnHolder.setPlaylist (playlist);
            holder.bt_settings.setOnClickListener (clickOnHolder);
        }
    }
    private class ClickOnHolder implements View.OnClickListener {

        private String playlist;

        private void setPlaylist(String playlist) {this.playlist = playlist;}

        @Override
        public void onClick (View v) {
            ClickOnPlaylist.longClickPlaylist (playlist);

        }
    }

    @Override
    public int getItemCount() {
        return mesArtistes.size(); // pour ne pas être embete avec les tailles de liste
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private ImageView bt_settings;
        private ImageView album;

        public MyViewHolder(@NonNull View itemView) {
            // itemview = vue de chaque cellule
            super(itemView);

            if(isAlbum) {
                album = itemView.findViewById (R.id.album);
                album.setOnClickListener (new Click ());
                album.setOnLongClickListener (new LongClick ());

            }
            else {

                // afficher le nom de la musique courante
                mName = (TextView) itemView.findViewById (R.id.name);
                itemView.setOnClickListener (new Click ());
                itemView.setOnLongClickListener (new LongClick ());
                bt_settings = itemView.findViewById (R.id.bt_settings);
            }
        }
        public class Click implements View.OnClickListener {

            @Override
            public void onClick (View v) {
                if (mArtisteItemClickListener != null) {
                    mArtisteItemClickListener.onArtisteItemClick (
                            itemView,
                            (String) getItem (getAdapterPosition ()),
                            getAdapterPosition ());
                }
            }
        }

        public class LongClick implements  View.OnLongClickListener {

            @Override
            public boolean onLongClick (View v) {
                if (mArtisteItemClickListener != null) {
                    mArtisteItemClickListener.onArtisteItemLongClick (
                            itemView,
                            (String) getItem (getAdapterPosition ()),
                            getAdapterPosition ());
                }
                return false;
            }
        }
        void display(String artiste) {
            // ne jamais le mettre dans le constructeur
            if(isAlbum) {
            }
            else {
                mName.setText(artiste);
            }
        }
        void showImage(int position){
            RecupererImage image = new RecupererImage (mesAlbumsImages.get (position), context);
            try {
                album.setImageBitmap (image.recupImageMusique ());
            }
            catch (Exception e) {

            }
        }
    }
}
