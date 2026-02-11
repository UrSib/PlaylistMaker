package com.practicum.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.library.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity):Long

    @Query("SELECT * FROM play_list_table")
    fun getPlaylistsFlow(): Flow<List<PlaylistEntity>>

    @Query("UPDATE play_list_table SET playListTracksIds = :playListTracksIds, playListSize = :playListSize WHERE playListId = :playlistId")
    suspend fun updatePlaylist(playlistId: Long, playListTracksIds: String, playListSize: Int)
}