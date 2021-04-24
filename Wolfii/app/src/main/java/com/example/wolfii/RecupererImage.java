package com.example.wolfii;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;

import androidx.fragment.app.Fragment;

public class RecupererImage {
    private String path;
    private Context context;
    RecupererImage(String path, Context context) {
        this.path = path;
        this.context = context;
    }

    public Bitmap drawableEnBitmap (int drawableRes) {


        Drawable drawable = context.getResources ().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;

    }
    public Bitmap recupImageMusique() {
        MediaMetadataRetriever mediaMetadataRechercheur = new MediaMetadataRetriever();
        mediaMetadataRechercheur.setDataSource(path);

        byte [] image = mediaMetadataRechercheur.getEmbeddedPicture();

        mediaMetadataRechercheur.release();

        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }


}
