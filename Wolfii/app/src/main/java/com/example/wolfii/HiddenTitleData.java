package com.example.wolfii;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

// define table name
@Entity(tableName = "hiddenTitle")
public class HiddenTitleData implements Serializable {
    // create id column

    //String name, String author, String path, String duration, String dateTaken
    @PrimaryKey(autoGenerate = true)
    private int id;

    // create text column
    @ColumnInfo(name = "path")
    private String path;

    public int getId () {
        return id;
    }

    public String getPath () {
        return path;
    }

    public void setPath (String path) {
        this.path = path;
    }

    public void setId (int id) {
        this.id = id;
    }
}
