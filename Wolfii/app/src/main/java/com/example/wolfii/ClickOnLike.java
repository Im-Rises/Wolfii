package com.example.wolfii;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import static com.example.wolfii.MainActivity.database;
import static com.example.wolfii.MainActivity.mService;

public class ClickOnLike implements View.OnClickListener{
    private Musique musique;

    private Context context;

    private ImageView like;

    public void setMusique(Musique musique){this.musique = musique;}
    public void setLike(ImageView like) {this.like = like;}
    public void setContext(Context context) {this.context = context;}

    @Override
    public void onClick (View v) {

        try {
            DataLikedMusic dataLikedMusic = new DataLikedMusic ();
            dataLikedMusic.setPath (musique.getPath ());

            if (! database.mainDao ().getLikes ().contains (musique.getPath ())) {
                database.mainDao ().insertMusic (musique);
                database.mainDao ().insertLike (dataLikedMusic);
                like.setImageBitmap (drawableEnBitmap (R.drawable.like_white));
            } else {
                database.mainDao ().deleteLike (dataLikedMusic);

                like.setImageBitmap (drawableEnBitmap (R.drawable.unlike_white));
            }

            if (mService.getMusiquePlayerIsSet () && mService.getMusiquePlayerPath () == musique.getPath ())
                mService.notificationInitEtMaj ();
        }
        catch (Exception e){
            Log.d("debug_noLike", e.getStackTrace ().toString ());
        }

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
}
