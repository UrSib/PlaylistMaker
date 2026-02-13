package com.practicum.playlistmaker.library.data.db.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "play_list_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playListId: Long,
    val playListName: String,
    val playListDescription: String?,
    val playListCoverPath: String?,
    val playListTracksIds: String,
    val playListSize: Int
)