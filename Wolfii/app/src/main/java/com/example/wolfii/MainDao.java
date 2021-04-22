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

    @Query ("DELETE FROM Wolfii WHERE playlist = :sPlaylist")
    void deletePlaylist(String sPlaylist);

    @Query ("UPDATE Wolfii SET playlist = :newName WHERE playlist = :sPlaylist")
    void rename(String sPlaylist, String newName);
    /*
    //update query
    @Query("UPDATE playlists SET text = :sText where ID = :sID")
    void update(int sID, String sText);
     */


    // get all data query
    @Query("SELECT * FROM Wolfii")
    List<MainData> getAll();

    @Query ("SELECT playlist FROM Wolfii")
    List<String> getAllPlaylists();

    @Query ("SELECT * FROM Wolfii WHERE playlist= :sPlaylist")
    List<MainData> getMusicFromPlaylist(String sPlaylist);


}
