package cinema;

import cinema.service.BookingService;
import cinema.ui.LoginFrame;
import javax.swing.*;

// Starting point of the app. Exp 5: uses classes from our own packages.
public class Main {
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());   // same look on every OS
        BookingService service = new BookingService();
        SwingUtilities.invokeLater(() -> new LoginFrame(service).setVisible(true));
    }
}
