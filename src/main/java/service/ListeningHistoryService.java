package service;

import domain.Playlist;

import java.time.YearMonth;

public interface ListeningHistoryService {
    Playlist getPlaylistOfTopTracksOfTheMonth(YearMonth month);
    Playlist getPlaylistOfTopTracksOfTheMonthWithoutCachedTracks(YearMonth month);
    Playlist getRandomTracks(YearMonth month);
}
