package com.example.wolfii;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.example.wolfii.MainActivity.database;

public class MyMusiqueAdapter extends RecyclerView.Adapter<MyMusiqueAdapter.MyViewHolder> {
    // classe qui est responsable de chaque cellule
    // responsable du recyclage des view
    // view holder = accelerer le rendu de la liste, il sera déclaré au sein de l'adapter
    private ArrayList<Musique> mesMusiques;
    public static Context context;

    private int positionMusique = -1;
    private Boolean positionMusiqueIsSet = false;

    public List<Musique> getMesMusiques() {
        return mesMusiques;
    }

    public void setMesMusiques(ArrayList<Musique> mesMusiques) {
        this.mesMusiques = mesMusiques;
    }
    public void setPositionMusique(int positionMusique){this.positionMusique = positionMusique; this.positionMusiqueIsSet=true;}

    public MyMusiqueAdapter(ArrayList<Musique> mesMusiques, Context context) {
        this.mesMusiques = mesMusiques;
        this.context = context;
    }

    public Object getItem(int position) {
        return mesMusiques.get(position);
    }

    public interface MusiqueItemClickListener {
        void onMusiqueItemClick(View view, Musique musique, int position);
        void onMusiqueItemLongClick(View view, Musique musique, int position);
    }
    private MusiqueItemClickListener mMusiqueItemClickListener;

    public void setmMusiqueItemClickListener(MusiqueItemClickListener mMusiqueItemClickListener) {
        this.mMusiqueItemClickListener = mMusiqueItemClickListener;
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // affiche les viewholder en donnant la position
        holder.display(mesMusiques.get(position), position == positionMusique, positionMusiqueIsSet);
        Log.d("position", position + "");
        Musique musique = mesMusiques.get(position);
        ClickOnHolder clickOnHolder = new ClickOnHolder ();
        clickOnHolder.setMusique (musique);
        holder.bt_settings.setOnClickListener(clickOnHolder);

        ClickOnLike clickOnLike = new ClickOnLike ();
        clickOnLike.setPath(mesMusiques.get(position).getPath ());
        clickOnLike.setLike (holder.like);
        clickOnLike.setContext(context);
        holder.like.setOnClickListener (clickOnLike);
    }

    private class ClickOnHolder implements View.OnClickListener {
        private Musique musique;

        private void setMusique(Musique musique) {this.musique = musique;}

        @Override
        public void onClick (View v) {
            ClickOnMusic.longClickMusic (musique);
        }
    }
    @Override
    public int getItemCount() {
        return mesMusiques.size(); // pour ne pas être embete avec les tailles de liste
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private ImageView bt_settings, like, album;
        private List<String> likedMusic = database.mainDao ().getLikes ();
        public MyViewHolder(@NonNull View itemView) {
            // itemview = vue de chaque cellule
            super(itemView);

            // afficher le nom de la musique courante
            mName = (TextView) itemView.findViewById(R.id.name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMusiqueItemClickListener != null) {
                        mMusiqueItemClickListener.onMusiqueItemClick(
                                itemView,
                                (Musique) getItem(getAdapterPosition()),
                                getAdapterPosition());
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mMusiqueItemClickListener != null) {
                        mMusiqueItemClickListener.onMusiqueItemLongClick(
                                itemView,
                                (Musique)getItem(getAdapterPosition()),
                                getAdapterPosition());
                    }
                    return false;
                }
            });
            bt_settings = itemView.findViewById(R.id.bt_settings);
            like = itemView.findViewById (R.id.like);
            album = itemView.findViewById (R.id.image_album);
        }
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        void display(Musique musique, boolean isCurrentMusic, Boolean positionMusicIsSet) {
            // ne jamais le mettre dans le constructeur
            mName.setText(musique.getName());
            if(positionMusicIsSet) {
                if (isCurrentMusic) {
                    mName.setTextColor (Color.rgb(147,112,219));
                    mName.setTextAlignment (View.TEXT_ALIGNMENT_CENTER);
                    mName.setTextSize (20);
                }
            }
            if(likedMusic.contains (musique.getPath ())) {
                like.setImageBitmap (drawableEnBitmap (R.drawable.like));
            }
            album.setImageBitmap (recupImageMusiquePageControle (musique.getPath ()));

        }
        public Bitmap drawableEnBitmap (int drawableRes) {
            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = context.getResources().getDrawable(drawableRes);
            Canvas canvas = new Canvas();
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            canvas.setBitmap(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);

            return bitmap;
        }
        public byte[] recupImageMusiqueByte(String path)
        {
            MediaMetadataRetriever mediaMetadataRechercheur = new MediaMetadataRetriever();
            mediaMetadataRechercheur.setDataSource(path);

            byte [] image = mediaMetadataRechercheur.getEmbeddedPicture();

            mediaMetadataRechercheur.release();

            return image;
        }
        public Bitmap recupImageMusiquePageControle(String path) {

            byte [] image = recupImageMusiqueByte(path);

            if (image!=null)
                //Si une image n'est trouvé dans le fichier audio
                return BitmapFactory.decodeByteArray(image, 0, image.length);
            else
            {
                //Si aucune image n'est trouvé dans le fichier mp3 alors on met le logo logoStyle.png
                return drawableEnBitmap(R.drawable.logostyle);
            }
        }


    }
}
