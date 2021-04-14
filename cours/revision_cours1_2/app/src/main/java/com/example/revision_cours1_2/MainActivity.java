package com.example.revision_cours1_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity {
    GoogleMap map;

    public static final String EXTRA_MESSAGE = "com.eample.myfirstapp.Message";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editTextTextPersonName4);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    public void toDoCoord(View view) {
        LatLng sydney = new LatLng(80,80);
        this.map.addMarker(new MarkerOptions().position(sydney).title("equateur"));
        this.map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}