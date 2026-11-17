package cinema.service;

import cinema.model.BookingException;

// Exp 4: interface. Every payment type must have these two methods.
public interface Payable {
    void pay(double amount) throws BookingException;
    String getMethodName();

    static Payable create(String method, String details) {   // makes a UPI or a card payment
        return method.equals("UPI") ? new UpiPayment(details) : new CardPayment(details);
    }
}

// Exp 4: two classes that implement the interface (pretend payments, no real money is taken)
class UpiPayment implements Payable {
    private String upiId;
    UpiPayment(String upiId) { this.upiId = upiId.trim(); }
    public String getMethodName() { return "UPI"; }
    public void pay(double amount) throws BookingException {
        if (!upiId.matches("[^@ ]+@[^@ ]+")) throw new BookingException("Payment failed: UPI ID should look like name@bank");
        System.out.printf("Paid Rs. %.2f by UPI from %s%n", amount, upiId);
    }
}

class CardPayment implements Payable {
    private String cardNumber;
    CardPayment(String cardNumber) { this.cardNumber = cardNumber.replace(" ", ""); }
    public String getMethodName() { return "Card"; }
    public void pay(double amount) throws BookingException {
        if (!cardNumber.matches("[0-9]{16}")) throw new BookingException("Payment failed: card number should have 16 digits");
        System.out.printf("Paid Rs. %.2f by card ending in %s%n", amount, cardNumber.substring(12));
    }
}
