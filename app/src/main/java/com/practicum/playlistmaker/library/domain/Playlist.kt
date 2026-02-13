package com.practicum.playlistmaker.library.domain

class Playlist(
    val playListId: Long,
    val playListName: String,
    val playListDescription: String?,
    val playListCoverPath: String?,
    var playListTracksIds: String,
    var playListSize: Int
)