package com.example.wolfii;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "PlaylistMusic",
        primaryKeys = {"path", "playlist"},
        foreignKeys = {
            @ForeignKey (entity = DataMusique.class, parentColumns = "path", childColumns = "path"),
            @ForeignKey (entity = DataPlaylist.class, parentColumns = "nom", childColumns = "playlist")})
public class DataPlaylistMusic {
    @ColumnInfo(name = "path")
    @NonNull
    private String path;

    @ColumnInfo(name = "playlist")
    @NonNull
    private String playlist;

    public String getPath () {
        return path;
    }

    public void setPath (String path) {
        this.path = path;
    }

    public String getPlaylist () {
        return playlist;
    }

    public void setPlaylist (String playlist) {
        this.playlist = playlist;
    }
}
