package cinema.model;

// Exp 6: user-defined exception for a wrong login, a booked seat or a failed payment
public class BookingException extends Exception {
    public BookingException(String message) {
        super(message);
    }
}
