package com.example.wolfii;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface MainDao {
    // insert query
    @Insert(onConflict = REPLACE)
    void insert(MainData mainData);

    // delete query
    @Delete
    void delete(MainData mainData);

    // delete all query
    @Delete
    void reset(List<MainData> mainData);

    /*
    //update query
    @Query("UPDATE playlists SET text = :sText where ID = :sID")
    void update(int sID, String sText);
     */


    // get all data query
    @Query("SELECT * FROM playlists")
    List<MainData> getAll();

    @Query ("SELECT playlist FROM playlists")
    List<String> getAllPlaylists();

    @Query ("SELECT * FROM playlists WHERE playlist= :sPlaylist")
    List<MainData> getMusicFromPlaylist(String sPlaylist);


}
