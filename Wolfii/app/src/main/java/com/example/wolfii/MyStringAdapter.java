package com.example.wolfii;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.example.wolfii.MainActivity.database;

public class MyStringAdapter extends RecyclerView.Adapter<MyStringAdapter.MyViewHolder> {
    // classe qui est responsable de chaque cellule
    // responsable du recyclage des view
    // view holder = accelerer le rendu de la liste, il sera déclaré au sein de l'adapter
    List<String> mesArtistes;
    public static Context context;
    Boolean isPlaylist;



    public MyStringAdapter (ArrayList<String> mesArtistes, Context sContext, Boolean isPlaylist) {
        this.mesArtistes = mesArtistes;
        this.isPlaylist = isPlaylist;
    }

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
        View view = layoutInflater.inflate(R.layout.musique_item, parent, false);
        // on renvoie le viewholder
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // affiche les viewholder en donnant la position
        holder.display(mesArtistes.get(position));
        Log.d("position", position + "");
        if(isPlaylist) {
            String playlist = mesArtistes.get (position);
            holder.bt_settings.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    Dialog dialog = new Dialog (context);
                    // set content view
                    dialog.setContentView (R.layout.dialog_playlist);

                    // initialize width and height
                    int width = WindowManager.LayoutParams.MATCH_PARENT;
                    int height = WindowManager.LayoutParams.WRAP_CONTENT;
                    //set layout
                    dialog.getWindow ().setLayout (width, height);
                    dialog.show ();

                    Button delete = dialog.findViewById (R.id.delete);
                    Button rename = dialog.findViewById (R.id.rename);

                    delete.setOnClickListener (new View.OnClickListener () {
                        public void onClick (View v) {
                            database.mainDao ().deletePlaylist (playlist);
                            Toast.makeText (context, "playlist " + playlist + " supprimée", Toast.LENGTH_SHORT).show ();
                        }
                    });

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mesArtistes.size(); // pour ne pas être embete avec les tailles de liste
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private ImageView bt_settings;

        public MyViewHolder(@NonNull View itemView) {
            // itemview = vue de chaque cellule
            super(itemView);

            // afficher le nom de la musique courante
            mName = (TextView) itemView.findViewById(R.id.name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mArtisteItemClickListener != null) {
                        mArtisteItemClickListener.onArtisteItemClick(
                                itemView,
                                (String) getItem(getAdapterPosition()),
                                getAdapterPosition());
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mArtisteItemClickListener != null) {
                        mArtisteItemClickListener.onArtisteItemLongClick(
                                itemView,
                                (String)getItem(getAdapterPosition()),
                                getAdapterPosition());
                    }
                    return false;
                }
            });
            bt_settings = itemView.findViewById(R.id.bt_settings);
        }
        void display(String artiste) {
            // ne jamais le mettre dans le constructeur
            mName.setText(artiste);
        }

    }
}
