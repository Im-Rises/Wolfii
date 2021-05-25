package com.example.wolfii;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "LikedMusic", foreignKeys = {@ForeignKey (entity = DataMusique.class, parentColumns = "path", childColumns = "path")})
public class DataLikedMusic {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "path")
    @NonNull
    private String path;

    public String getPath () {
        return path;
    }

    public void setPath (String path) {
        this.path = path;
    }
}
