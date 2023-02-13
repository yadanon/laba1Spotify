package service;

import domain.Playlist;
import domain.Track;
import domain.TrackHistory;
import lombok.AllArgsConstructor;
import util.DateUtils;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public class ListeningHistory implements ListeningHistoryService {

    private final List<TrackHistory> allTrackHistory;
    private static final Integer NUMBER_OF_TOP_TRACKS = 3;

    @Override
    public Playlist getPlaylistOfTopTracksOfTheMonth(YearMonth month) {
        Playlist newPlaylist = new Playlist(new ArrayList<>());
        getStreamOfSortedTracksByPlays(month)
                .limit(NUMBER_OF_TOP_TRACKS.longValue())
                .forEach(entry -> newPlaylist.getTracks().add(entry.getKey().getTrack()));
        return newPlaylist;
    }

    @Override
    public Playlist getPlaylistOfTopTracksOfTheMonthWithoutCachedTracks(YearMonth month) {
        Playlist newPlaylist = new Playlist(new ArrayList<>());
        Playlist oldPlaylist = getPlaylistOfTopTracksOfTheMonth(month);
        getStreamOfSortedTracksByPlays(month.minusMonths(1L))
                .filter(trackEntry -> !oldPlaylist.getTracks().contains(trackEntry.getKey().getTrack()))
                .limit(NUMBER_OF_TOP_TRACKS.longValue())
                .forEach(entry -> newPlaylist.getTracks().add(entry.getKey().getTrack()));
        return newPlaylist;
    }

    @Override
    public Playlist getRandomTracks(YearMonth month) {
        Playlist newPlaylist = new Playlist(new ArrayList<>());
        Track trackFromTheFirstPlaylist = getPlaylistOfTopTracksOfTheMonth(month).getTracks().get(0);
        Track trackFromTheSecondPlaylist = getPlaylistOfTopTracksOfTheMonthWithoutCachedTracks(month).getTracks().get(0);
        newPlaylist.getTracks().add(trackFromTheFirstPlaylist);
        newPlaylist.getTracks().add(trackFromTheSecondPlaylist);
        getStreamOfSortedTracksByPlays(month)
                .filter(entry -> !newPlaylist.getTracks().contains(entry.getKey().getTrack()))
                .limit(3L)
                .forEach(entry -> newPlaylist.getTracks().add(entry.getKey().getTrack()));
        return newPlaylist;
    }

    private Stream<Map.Entry<TrackHistory, Long>> getStreamOfSortedTracksByPlays(YearMonth month) {
        return allTrackHistory.stream()
                .collect(
                        Collectors.toMap(trackHistory -> trackHistory,
                                trackHistory -> countTrackPlaysInMonth(trackHistory, month))
                ).entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
    }

    private Long countTrackPlaysInMonth(TrackHistory trackHistory ,YearMonth month) {
        return trackHistory.getPlayDates().stream()
                .filter(date -> DateUtils.isDateInMonth(date, month))
                .count();
    }
}
