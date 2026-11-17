package cinema.ui;

import cinema.model.*;
import cinema.service.BookingService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

// Exp 10: movie list and the My Bookings table. Exp 11: ActionListener for the two buttons.
public class MovieFrame extends JFrame implements ActionListener {
    private BookingService service;
    private User user;
    private JList<Movie> movieList;
    private JButton seatsButton = new JButton("Select Seats"), bookingsButton = new JButton("My Bookings");

    public MovieFrame(BookingService service, User user) {
        super("Movie Ticket Booking - Now Showing");
        this.service = service;
        this.user = user;
        JLabel welcome = new JLabel(user.getWelcomeMessage());
        welcome.setFont(new Font("SansSerif", Font.BOLD, 16));
        JPanel top = new JPanel(new GridLayout(2, 1, 0, 4));
        top.add(welcome);
        top.add(new JLabel("Pick a movie and click Select Seats. Rows D and E are premium seats (Rs. 100 extra)."));
        movieList = new JList<>(service.getMovies().toArray(new Movie[0]));
        movieList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        movieList.setFixedCellHeight(34);
        movieList.setSelectedIndex(0);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(bookingsButton);
        buttons.add(seatsButton);
        bookingsButton.addActionListener(this);
        seatsButton.addActionListener(this);
        JPanel main = new JPanel(new BorderLayout(0, 12));
        main.setBorder(new EmptyBorder(15, 18, 15, 18));
        main.add(top, BorderLayout.NORTH);
        main.add(new JScrollPane(movieList), BorderLayout.CENTER);
        main.add(buttons, BorderLayout.SOUTH);
        add(main);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(680, 360);
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == seatsButton && movieList.getSelectedValue() != null) {
            new SeatDialog(this, service, user, movieList.getSelectedValue()).setVisible(true);   // waits until closed
        } else if (e.getSource() == bookingsButton) {
            showMyBookings();
        }
        movieList.repaint();   // seat counts may have changed
    }

    // Shows the user's bookings in a table. After a cancel the table opens again with the new list.
    private void showMyBookings() {
        String[] columns = {"Booking ID", "Movie", "Show Time", "Seats", "Amount (Rs.)", "Paid By"};
        while (true) {
            List<Booking> list = service.getBookingsOf(user);
            String[][] rows = new String[list.size()][];
            for (int i = 0; i < rows.length; i++) {
                Booking b = list.get(i);
                Movie m = service.getMovie(b.getMovieId());
                rows[i] = new String[]{b.getBookingId(), m.getTitle(), m.getShowTime(), b.getSeatText(),
                        String.format("%.2f", b.getAmount()), b.getPaymentMethod()};
            }
            JTable table = new JTable(rows, columns);
            table.setDefaultEditor(Object.class, null);   // the cells cannot be typed into
            table.setRowHeight(26);
            JScrollPane pane = new JScrollPane(table);
            pane.setPreferredSize(new Dimension(660, 180));
            String[] options = {"Cancel Selected Booking", "Close"};
            int choice = JOptionPane.showOptionDialog(this, pane, "My Bookings", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
            int r = table.getSelectedRow();
            if (choice != 0) return;
            if (r < 0) JOptionPane.showMessageDialog(this, "Please select a booking first.");
            else if (JOptionPane.showConfirmDialog(this, "Cancel booking " + rows[r][0] + "?", "Confirm",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION && service.cancelBooking(rows[r][0], user))
                JOptionPane.showMessageDialog(this, "Booking cancelled. The seats are free again.");
        }
    }
}
