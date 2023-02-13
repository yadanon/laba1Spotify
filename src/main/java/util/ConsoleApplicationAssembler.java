package util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import domain.TrackHistory;
import service.ListeningHistory;
import service.ListeningHistoryService;

import java.time.YearMonth;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ConsoleApplicationAssembler {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final Scanner COMMAND_SCANNER = new Scanner(System.in);

    private static List<TrackHistory> allTrackHistory;
    private static ListeningHistoryService listeningHistoryService;

    static {
        try {
            allTrackHistory = OBJECT_MAPPER.readValue(
                    ClassLoader.getSystemClassLoader().getResourceAsStream("init.json"),
                    new TypeReference<List<TrackHistory>>(){});
        } catch (Exception e) {
            System.err.println("Error caught when using ObjectMapper: " + e.getMessage());
        }
        listeningHistoryService = new ListeningHistory(allTrackHistory);
    }

    public static void runApplication() {
        int running = 1;
        System.out.println("HELLO! CHOOSE YOUR PLAYLIST)");
        while(running == 1) {

            System.out.println("1 - TOP TRACKS OF THIS MONTH");
            System.out.println("2 - TOP TRACKS OF PREVIOUS MONTH");
            System.out.println("3 - MIX FOR YOU");
            System.out.println("4 - CLOSE APP");

            try {
                switch (COMMAND_SCANNER.nextInt()) {
                    case 1 -> showInConsoleTopTracksOfTheMonth();
                    case 2 -> showInConsoleTopTracksOfPreviousMonth();
                    case 3 -> showInConsoleRandomTracks();
                    case 4 -> running = 0;
                    default -> System.err.println("PLEASE, ENTER CORRECT NUMBER");
                }
            } catch (InputMismatchException exception) {
                System.err.println("PLEASE, ENTER A NUMBER");
                COMMAND_SCANNER.next();
            }
        }
        COMMAND_SCANNER.close();
    }

    private static void showInConsoleTopTracksOfPreviousMonth() {
        listeningHistoryService.getPlaylistOfTopTracksOfTheMonthWithoutCachedTracks(YearMonth.now())
                .getTracks()
                .forEach(System.out::println);
    }

    private static void showInConsoleTopTracksOfTheMonth() {
        listeningHistoryService.getPlaylistOfTopTracksOfTheMonth(YearMonth.now())
                .getTracks()
                .forEach(System.out::println);
    }

    private static void showInConsoleRandomTracks() {
        listeningHistoryService.getRandomTracks(YearMonth.now())
                .getTracks()
                .forEach(System.out::println);
    }
}
