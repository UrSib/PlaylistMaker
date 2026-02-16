package com.practicum.playlistmaker.library.domain

class Playlist(
    val playListId: Long,
    var playListName: String,
    var playListDescription: String?,
    var playListCoverPath: String?,
    var playListTracksIds: String,
    var playListSize: Int
)