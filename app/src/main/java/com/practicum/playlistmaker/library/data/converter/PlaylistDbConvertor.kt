package com.practicum.playlistmaker.library.data.converter

import com.practicum.playlistmaker.library.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.library.domain.Playlist


class PlaylistDbConvertor {

    fun map(playList: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playList.playListId,
            playList.playListName,
            playList.playListDescription,
            playList.playListCoverPath,
            playList.playListTracksIds,
            playList.playListSize
        )
    }

    fun map(playList: PlaylistEntity): Playlist{
        return Playlist(
            playList.playListId,
            playList.playListName,
            playList.playListDescription,
            playList.playListCoverPath,
            playList.playListTracksIds,
            playList.playListSize
        )
    }

}