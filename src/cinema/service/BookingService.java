package cinema.service;

import cinema.model.*;
import java.util.*;

// The main logic of the app. The screens in the ui package only call methods of this class.
public class BookingService {
    private Map<String, Movie> movies = new LinkedHashMap<>();   // Exp 9: collections
    private HashMap<String, User> users = FileHandler.loadUsers();
    private ArrayList<Booking> bookings = FileHandler.loadBookings();

    public BookingService() {
        Movie[] shows = {new Movie("M1", "Interstellar", "English", "10:00 AM", 200),
                new Movie("M2", "3 Idiots", "Hindi", "01:30 PM", 180),
                new Movie("M3", "Baahubali", "Telugu", "05:00 PM", 220),
                new Movie("M4", "Inception", "English", "09:00 PM", 250)};
        for (Movie m : shows) movies.put(m.getMovieId(), m);
        for (Booking b : bookings) markSeats(b, true);   // seats of saved bookings are booked again
    }

    private void markSeats(Booking b, boolean booked) {
        Movie movie = movies.get(b.getMovieId());
        for (String id : b.getSeatIds()) {
            if (movie != null && movie.getSeat(id) != null) movie.getSeat(id).setBooked(booked);
        }
    }

    public List<Movie> getMovies() { return new ArrayList<>(movies.values()); }
    public Movie getMovie(String movieId) { return movies.get(movieId); }

    // Exp 6: throws our own exception when the login is wrong
    public User login(String username, String password) throws BookingException {
        User user = users.get(username);
        if (user == null || !user.checkPassword(password)) throw new BookingException("Wrong username or password.");
        return user;
    }

    // Exp 2: method overloading. Two methods with the same name but different parameters.
    public double calculatePrice(List<Seat> seats) {
        double total = 0;
        for (Seat seat : seats) total += seat.getPrice();
        return total;
    }

    public double calculatePrice(List<Seat> seats, User user) {   // also takes off the member discount
        double total = calculatePrice(seats);
        return total - total * user.getDiscountPercent() / 100;
    }

    public void checkSeat(Movie movie, String seatId) throws BookingException {
        if (movie.getSeat(seatId).isBooked())
            throw new BookingException("Seat " + seatId + " is already booked. Please pick another seat.");
    }

    // Exp 8: synchronized lets only one thread in at a time, so one seat is never sold twice
    public synchronized Booking bookSeats(User user, Movie movie, List<String> seatIds, String method,
                                          String payDetails) throws BookingException {
        List<Seat> seats = new ArrayList<>();
        for (String id : seatIds) {
            checkSeat(movie, id);
            seats.add(movie.getSeat(id));
        }
        Payable payment = Payable.create(method, payDetails);   // Exp 4: a UpiPayment or a CardPayment
        double amount = calculatePrice(seats, user);
        payment.pay(amount);
        Booking booking = new Booking(user.getUsername(), movie.getMovieId(), seatIds, amount, payment.getMethodName());
        bookings.add(booking);
        markSeats(booking, true);
        FileHandler.write(FileHandler.BOOKINGS, Arrays.asList(booking.toFileLine()), true);
        return booking;
    }

    // Exp 9: Iterator lets us remove an item safely while going through the list
    public synchronized boolean cancelBooking(String bookingId, User user) {
        Iterator<Booking> it = bookings.iterator();
        while (it.hasNext()) {
            Booking b = it.next();
            if (b.getBookingId().equals(bookingId) && b.getUsername().equals(user.getUsername())) {
                it.remove();
                markSeats(b, false);
                List<String> lines = new ArrayList<>();
                for (Booking left : bookings) lines.add(left.toFileLine());
                FileHandler.write(FileHandler.BOOKINGS, lines, false);
                return true;
            }
        }
        return false;
    }

    public synchronized List<Booking> getBookingsOf(User user) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : bookings) if (b.getUsername().equals(user.getUsername())) result.add(b);
        return result;
    }
}
