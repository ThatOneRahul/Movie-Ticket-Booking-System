package cinema.ui;

import cinema.model.*;
import cinema.service.BookingService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

// Exp 10: Swing login form. Exp 11: this class is the ActionListener for the Login button.
public class LoginFrame extends JFrame implements ActionListener {
    private BookingService service;
    private JTextField userField = new JTextField(15);
    private JPasswordField passField = new JPasswordField(15);
    private JButton loginButton = new JButton("Login");

    public LoginFrame(BookingService service) {
        super("Movie Ticket Booking - Login");
        this.service = service;
        JLabel heading = new JLabel("Movie Ticket Booking", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        for (Component c : new Component[]{new JLabel("Username:"), userField, new JLabel("Password:"), passField,
                new JLabel(), loginButton}) form.add(c);
        JLabel hint = new JLabel("Try  demo / demo123  or  premium / premium123", SwingConstants.CENTER);
        hint.setForeground(Color.GRAY);
        JPanel main = new JPanel(new BorderLayout(0, 16));
        main.setBorder(new EmptyBorder(20, 30, 15, 30));
        main.add(heading, BorderLayout.NORTH);
        main.add(form, BorderLayout.CENTER);
        main.add(hint, BorderLayout.SOUTH);
        add(main);
        loginButton.addActionListener(this);
        passField.addActionListener(this);   // pressing Enter in the password box also logs in
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {   // Exp 6: login throws BookingException when the username or password is wrong
            User user = service.login(userField.getText().trim(), new String(passField.getPassword()));
            new MovieFrame(service, user).setVisible(true);
            dispose();
        } catch (BookingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
