package cinema.model;

import java.util.ArrayList;
import java.util.List;

// One confirmed booking. Exp 1: two constructors. Exp 2: static counter for the ids.
public class Booking {
    private static int counter = 1000;   // static: one copy shared by all Booking objects
    private String bookingId, username, movieId, paymentMethod;
    private List<String> seatIds;
    private double amount;

    // for a new booking: the next id is made automatically
    public Booking(String username, String movieId, List<String> seatIds, double amount, String paymentMethod) {
        this("BK" + (counter + 1), username, movieId, seatIds, amount, paymentMethod);
    }

    // for a booking loaded from the file: the id is already known
    public Booking(String bookingId, String username, String movieId, List<String> seatIds,
                   double amount, String paymentMethod) {
        this.bookingId = bookingId;
        this.username = username;
        this.movieId = movieId;
        this.seatIds = new ArrayList<>(seatIds);
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        counter = Math.max(counter, Integer.parseInt(bookingId.substring(2)));   // may throw NumberFormatException
    }

    public String toFileLine() {   // for example  BK1001,demo,M1,A1;A2,400.0,UPI
        return String.join(",", bookingId, username, movieId, String.join(";", seatIds),
                String.valueOf(amount), paymentMethod);
    }

    public String getBookingId() { return bookingId; }
    public String getUsername() { return username; }
    public String getMovieId() { return movieId; }
    public List<String> getSeatIds() { return seatIds; }
    public String getSeatText() { return String.join(", ", seatIds); }
    public double getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
}
