package cinema.model;

import java.util.LinkedHashMap;
import java.util.Map;

// Exp 1: class with a parameterized constructor. Each movie is one show with 40 seats.
public class Movie {
    public static final char[] ROWS = {'A', 'B', 'C', 'D', 'E'};
    public static final int SEATS_PER_ROW = 8;
    private String movieId, title, language, showTime;
    private double basePrice;
    private Map<String, Seat> seats = new LinkedHashMap<>();   // Exp 9: seat id -> Seat

    public Movie(String movieId, String title, String language, String showTime, double basePrice) {
        this.movieId = movieId;
        this.title = title;
        this.language = language;
        this.showTime = showTime;
        this.basePrice = basePrice;
        for (char row : ROWS) {   // rows D and E at the back are premium seats
            for (int n = 1; n <= SEATS_PER_ROW; n++) seats.put(row + "" + n, Seat.create(basePrice, row >= 'D'));
        }
    }

    public Seat getSeat(String seatId) { return seats.get(seatId); }
    public String getMovieId() { return movieId; }
    public String getTitle() { return title; }
    public String getLanguage() { return language; }
    public String getShowTime() { return showTime; }
    public double getBasePrice() { return basePrice; }

    public int getAvailableSeatCount() {
        int count = 0;
        for (Seat seat : seats.values()) if (!seat.isBooked()) count++;
        return count;
    }

    @Override
    public String toString() {   // the text shown for this movie in the movie list
        return String.format("%-14s%-11s%-11sRs. %-6d%d seats left",
                title, "(" + language + ")", showTime, (int) basePrice, getAvailableSeatCount());
    }
}
