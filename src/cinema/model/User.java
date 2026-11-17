package cinema.model;

// Exp 4: abstract class. Exp 3: multilevel inheritance User -> Customer -> PremiumCustomer.
public abstract class User {
    protected String username, password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Exp 2: static method that makes the right kind of user from its type word
    public static User create(String username, String password, String type) {
        return type.equals("PREMIUM") ? new PremiumCustomer(username, password) : new Customer(username, password);
    }

    public String getUsername() { return username; }
    public boolean checkPassword(String input) { return password.equals(input); }
    public abstract double getDiscountPercent();   // abstract: each child class writes its own
    public abstract String getWelcomeMessage();
}

class Customer extends User {
    Customer(String username, String password) { super(username, password); }
    @Override public double getDiscountPercent() { return 0; }
    @Override public String getWelcomeMessage() { return "Welcome, " + username + "!"; }
}

class PremiumCustomer extends Customer {
    PremiumCustomer(String username, String password) { super(username, password); }
    @Override public double getDiscountPercent() { return 10; }
    @Override public String getWelcomeMessage() {   // reuses the Customer message
        return super.getWelcomeMessage() + "  (Premium member: 10% off every booking)";
    }
}
