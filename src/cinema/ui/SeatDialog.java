package cinema.ui;

import cinema.model.*;
import cinema.service.BookingService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

// Seat map and payment screen. Exp 10: grid of seat buttons. Exp 8: runs a countdown thread.
// Exp 11: ActionListener for the clicks and MouseListener for the hover color on the seats.
public class SeatDialog extends JDialog implements ActionListener, MouseListener {
    private static final Color NORMAL = new Color(205, 225, 250), PREMIUM = new Color(255, 222, 135),
            SELECTED = new Color(55, 155, 75), BOOKED = new Color(185, 65, 65), HOVER = new Color(165, 228, 185);
    private BookingService service;
    private User user;
    private Movie movie;
    private ArrayList<String> selected = new ArrayList<>();            // Exp 9: ids of the selected seats
    private HashMap<String, JButton> seatButtons = new HashMap<>();    // Exp 9: seat id -> its button
    private JLabel timerLabel = new JLabel("Time left: 2:00"), selectedLabel = new JLabel(), totalLabel = new JLabel();
    private JTextField payField = new JTextField(14);
    private JComboBox<String> payBox = new JComboBox<>(new String[]{"UPI", "Card"});
    private JButton backButton = new JButton("Back"), confirmButton = new JButton("Confirm and Pay");
    private CountdownTimer timer = new CountdownTimer();

    public SeatDialog(JFrame owner, BookingService service, User user, Movie movie) {
        super(owner, "Select Seats - " + movie.getTitle(), true);   // true = modal window
        this.service = service;
        this.user = user;
        this.movie = movie;
        JLabel title = new JLabel(movie.getTitle() + " (" + movie.getLanguage() + ")   " + movie.getShowTime());
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        timerLabel.setForeground(new Color(180, 60, 0));
        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.WEST);
        top.add(timerLabel, BorderLayout.EAST);
        JPanel grid = new JPanel(new GridLayout(Movie.ROWS.length, Movie.SEATS_PER_ROW + 1, 6, 6));
        for (char row : Movie.ROWS) {
            grid.add(new JLabel(String.valueOf(row), SwingConstants.CENTER));
            for (int n = 1; n <= Movie.SEATS_PER_ROW; n++) {
                JButton b = new JButton(row + "" + n);
                b.setPreferredSize(new Dimension(58, 38));
                b.setToolTipText(b.getText() + "  Rs. " + (int) movie.getSeat(b.getText()).getPrice());
                b.addActionListener(this);
                b.addMouseListener(this);
                seatButtons.put(b.getText(), b);
                grid.add(b);
            }
        }
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        legend.add(colorLabel("  Normal  Rs. " + (int) movie.getBasePrice() + "  ", NORMAL, Color.BLACK));
        legend.add(colorLabel("  Premium  Rs. " + (int) (movie.getBasePrice() + Seat.PREMIUM_EXTRA) + "  ", PREMIUM, Color.BLACK));
        legend.add(colorLabel("  Selected  ", SELECTED, Color.WHITE));
        legend.add(colorLabel("  Booked  ", BOOKED, Color.WHITE));
        JPanel payRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        for (Component c : new Component[]{new JLabel("Pay by:"), payBox, new JLabel("UPI ID / Card no:"), payField,
                Box.createHorizontalStrut(30), backButton, confirmButton}) payRow.add(c);
        backButton.addActionListener(this);
        confirmButton.addActionListener(this);
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        JPanel bottom = new JPanel(new GridLayout(4, 1, 0, 10));
        for (JComponent c : new JComponent[]{legend, selectedLabel, totalLabel, payRow}) bottom.add(c);
        JPanel main = new JPanel(new BorderLayout(10, 14));
        main.setBorder(new EmptyBorder(12, 16, 12, 16));
        main.add(top, BorderLayout.NORTH);
        main.add(grid, BorderLayout.CENTER);
        main.add(bottom, BorderLayout.SOUTH);
        add(main);
        refresh();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
        timer.setDaemon(true);   // the app can close even if this thread is still running
        timer.start();           // Exp 8: start the countdown thread
    }

    private static JLabel colorLabel(String text, Color background, Color textColor) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(background);
        label.setForeground(textColor);
        return label;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) dispose();
        else if (e.getSource() == confirmButton) confirm();
        else {   // one of the seat buttons
            String id = e.getActionCommand();
            try {
                service.checkSeat(movie, id);   // Exp 6: throws BookingException if the seat is taken
                if (selected.contains(id)) selected.remove(id);
                else selected.add(id);
            } catch (BookingException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Seat not available", JOptionPane.WARNING_MESSAGE);
            }
            refresh();
        }
    }

    private void confirm() {
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one seat.");
            return;
        }
        Booking booking = null;
        try {   // Exp 6: try, catch and finally
            booking = service.bookSeats(user, movie, selected, (String) payBox.getSelectedItem(), payField.getText());
            timer.stopTimer();
            JOptionPane.showMessageDialog(this, "Booking confirmed!\n\nBooking ID:  " + booking.getBookingId()
                    + "\nMovie:  " + movie.getTitle() + " at " + movie.getShowTime() + "\nSeats:  " + booking.getSeatText()
                    + String.format("\nPaid:  Rs. %.2f by %s", booking.getAmount(), booking.getPaymentMethod()));
        } catch (BookingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Booking failed", JOptionPane.ERROR_MESSAGE);
        } finally {   // runs every time, whether the booking worked or not
            selected.removeIf(id -> movie.getSeat(id).isBooked());
            refresh();
        }
        if (booking != null) dispose();
    }

    // paints every seat in its color and updates the list of selected seats and the price
    private void refresh() {
        List<Seat> seats = new ArrayList<>();
        for (String id : seatButtons.keySet()) {
            Seat seat = movie.getSeat(id);
            JButton b = seatButtons.get(id);
            b.setBackground(seat.isBooked() ? BOOKED : selected.contains(id) ? SELECTED : seat.isPremium() ? PREMIUM : NORMAL);
            b.setForeground(seat.isBooked() || selected.contains(id) ? Color.WHITE : Color.BLACK);
            if (selected.contains(id)) seats.add(seat);
        }
        double full = service.calculatePrice(seats);   // Exp 2: both overloaded methods are used here
        double total = service.calculatePrice(seats, user);
        selectedLabel.setText(selected.isEmpty() ? "Seats selected: none  (click a seat to select it)"
                : "Seats selected (" + selected.size() + "):  " + String.join(", ", selected));
        totalLabel.setText(String.format("Total: Rs. %.2f", total)
                + (total < full ? String.format("   (10%% member discount on Rs. %.2f)", full) : ""));
    }

    // Exp 11: MouseListener is an interface, so all five methods must be written
    public void mouseEntered(MouseEvent e) {
        JButton b = (JButton) e.getSource();
        if (!movie.getSeat(b.getText()).isBooked() && !selected.contains(b.getText())) b.setBackground(HOVER);
    }
    public void mouseExited(MouseEvent e) { refresh(); }
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}

    private void timeUp() {
        if (!isDisplayable()) return;   // the window is already closed
        timerLabel.setText("Time left: 0:00");
        selected.clear();
        refresh();
        JOptionPane.showMessageDialog(this, "Time is up! Your selected seats have been released.\nPlease try again.",
                "Time up", JOptionPane.WARNING_MESSAGE);
        dispose();
    }

    @Override
    public void dispose() {   // stops the timer thread whenever this window closes
        timer.stopTimer();
        super.dispose();
    }

    // Exp 8: an inner class that extends Thread. It counts down apart from the Swing thread,
    // so the screen does not freeze while it waits.
    private class CountdownTimer extends Thread {
        private int secondsLeft = 120;
        private volatile boolean running = true;   // volatile: both threads see the change at once

        @Override
        public void run() {
            for (; secondsLeft > 0 && running; secondsLeft--) {
                String text = String.format("Time left: %d:%02d", secondsLeft / 60, secondsLeft % 60);
                SwingUtilities.invokeLater(() -> timerLabel.setText(text));   // Swing is changed only on its own thread
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;   // stopTimer() was called
                }
            }
            if (running) SwingUtilities.invokeLater(() -> timeUp());
        }

        void stopTimer() {
            running = false;
            interrupt();
        }
    }
}
