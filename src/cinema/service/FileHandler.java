package cinema.service;

import cinema.model.Booking;
import cinema.model.User;
import java.io.*;
import java.util.*;

// Exp 7: file read/write. Exp 2: all methods are static, so no FileHandler object is needed.
// data/users.txt has one "username,password,TYPE" per line. data/bookings.txt has one booking per line.
public class FileHandler {
    static final String USERS = "data/users.txt", BOOKINGS = "data/bookings.txt";

    static List<String> readLines(String file) {
        List<String> lines = new ArrayList<>();
        if (!new File(file).exists()) return lines;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {   // closes the file by itself
            String line;
            while ((line = reader.readLine()) != null) if (!line.trim().isEmpty()) lines.add(line.trim());
        } catch (IOException e) {
            System.out.println("Could not read " + file + ": " + e.getMessage());
        }
        return lines;
    }

    // append = true adds the lines at the end, false writes the whole file again
    static void write(String file, List<String> lines, boolean append) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, append))) {
            for (String line : lines) writer.write(line + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Could not write " + file + ": " + e.getMessage());
        }
    }

    static HashMap<String, User> loadUsers() {
        new File("data").mkdir();   // the first run makes the data folder and two demo users
        if (!new File(USERS).exists()) write(USERS, Arrays.asList("demo,demo123,NORMAL", "premium,premium123,PREMIUM"), false);
        HashMap<String, User> users = new HashMap<>();
        for (String line : readLines(USERS)) {
            String[] p = line.split(",");
            if (p.length == 3) users.put(p[0], User.create(p[0], p[1], p[2]));
        }
        return users;
    }

    static ArrayList<Booking> loadBookings() {
        ArrayList<Booking> bookings = new ArrayList<>();
        for (String line : readLines(BOOKINGS)) {
            try {   // Exp 6: a damaged line is skipped instead of crashing the app
                String[] p = line.split(",");
                bookings.add(new Booking(p[0], p[1], p[2], Arrays.asList(p[3].split(";")), Double.parseDouble(p[4]), p[5]));
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                System.out.println("Skipping a damaged line: " + line);
            }
        }
        return bookings;
    }
}
