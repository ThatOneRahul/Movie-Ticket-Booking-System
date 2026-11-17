package cinema.model;

// Exp 1: class with a constructor. Exp 3: parent class for single inheritance.
public class Seat {
    public static final double PREMIUM_EXTRA = 100;   // Exp 2: static constant
    protected double price;
    protected boolean booked;

    public Seat(double price) { this.price = price; }

    // Exp 2: static method that makes a normal or a premium seat
    public static Seat create(double basePrice, boolean premium) {
        return premium ? new PremiumSeat(basePrice) : new Seat(basePrice);
    }

    public double getPrice() { return price; }
    public boolean isBooked() { return booked; }
    public void setBooked(boolean booked) { this.booked = booked; }
    public boolean isPremium() { return false; }
}

// Exp 3: single inheritance. PremiumSeat IS-A Seat that costs Rs. 100 more.
class PremiumSeat extends Seat {
    PremiumSeat(double basePrice) { super(basePrice); }
    @Override public double getPrice() { return price + PREMIUM_EXTRA; }
    @Override public boolean isPremium() { return true; }
}
