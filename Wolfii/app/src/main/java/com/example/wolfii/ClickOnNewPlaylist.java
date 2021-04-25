package com.example.wolfii;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;


import static com.example.wolfii.MainActivity.database;

public class ClickOnNewPlaylist implements View.OnClickListener {
    private Context context;

    public void setContext(Context context) {this.context = context;}

    @Override
    public void onClick (View v) {
        Dialog dialog = new Dialog(context);

        // set content view
        dialog.setContentView(R.layout.dialog_new_playlist);

        // initialize width and height
        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        //set layout
        dialog.getWindow().setLayout(width, height);
        dialog.show ();

        Button valider = dialog.findViewById (R.id.confirmCreate);
        EditText nom = dialog.findViewById (R.id.name);

        valider.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                PlaylistData data = new PlaylistData ();
                data.setNom (nom.getText ().toString ());

                database.mainDao ().createPlaylist (data);

            }
        });
    }
}