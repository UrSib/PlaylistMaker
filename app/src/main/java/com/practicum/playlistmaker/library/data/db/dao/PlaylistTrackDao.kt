package com.practicum.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.library.data.db.entity.PlaylistTrackEntity
import com.practicum.playlistmaker.library.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistTrack(playlistTrackEntity: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_track_entity")
    fun getTracksFlow(): Flow<List<PlaylistTrackEntity>>

    @Query("DELETE FROM playlist_track_entity WHERE trackId = :trackId")
    suspend fun deleteTrackById(trackId: Long)
}