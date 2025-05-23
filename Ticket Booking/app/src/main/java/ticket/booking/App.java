package ticket.booking;

import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.services.*;
import ticket.booking.Util.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class App {

    public static void main(String[] args) {
        System.out.println("Running Train Booking System");
        Scanner scanner = new Scanner(System.in);
        int option = 0;
        UserBookingService userBookingService;
        try {
            userBookingService = new UserBookingService();
        } catch (IOException ex) {
            System.out.println("There is something wrong");
            return;
        }
        while (option != 7) {
            System.out.println("Choose option");
            System.out.println("1. Sign up");
            System.out.println("2. Login");
            System.out.println("3. Fetch Bookings");
            System.out.println("4. Search Trains");
            System.out.println("5. Book a Seat");
            System.out.println("6. Cancel my Booking");
            System.out.println("7. Exit the App");
            System.out.print("Enter your option: ");
            option = scanner.nextInt();
            Train trainSelectedForBooking = new Train();
            switch (option) {
                case 1:
                    System.out.println("Enter the username to signup");
                    String nameToSignUp = new Scanner(System.in).nextLine();
                    System.out.println("Enter the password to signup");
                    String passwordToSignUp = new Scanner(System.in).nextLine();
                    User userToSignup = new User(nameToSignUp, passwordToSignUp, UserServiceUtil.hashPassword(passwordToSignUp), new ArrayList<>(), UUID.randomUUID().toString());
                    userBookingService.signUp(userToSignup);
                    break;
                case 2:
                    System.out.println("Enter the username to Login");
                    String nameToLogin = new Scanner(System.in).nextLine();
                    System.out.println("Enter the password to signup");
                    String passwordToLogin = new Scanner(System.in).nextLine();
                    User userToLogin = new User(nameToLogin, passwordToLogin, UserServiceUtil.hashPassword(passwordToLogin), new ArrayList<>(), UUID.randomUUID().toString());
                    try {
                        userBookingService = new UserBookingService(userToLogin);
                        if (userBookingService.loginUser().equals(Boolean.TRUE))
                            System.out.println("User logged in successfully");
                        else
                            System.out.println("User not found");
                    } catch (Exception ex) {
                        System.out.println("There is something wrong" + ex.getMessage());
                        return;
                    }
                    break;
                case 3:
                    System.out.println("Fetching your bookings");
                    userBookingService.fetchBookings();
                    break;
                case 4:
                    System.out.println("Type your source station");
                    String source = new Scanner(System.in).nextLine();
                    System.out.println("Type your destination station");
                    String dest = new Scanner(System.in).nextLine();
                    List<Train> trains = userBookingService.getTrains(source, dest);
                    if (trains.size() == 0) {
                        System.out.println("No trains found");
                        break;
                    }
                    int index = 1;
                    for (Train t : trains) {
                        System.out.println(index + " Train id : " + t.getTrainId());
                        for (Map.Entry<String, String> entry : t.getStationTimes().entrySet()) {
                            System.out.println("station " + entry.getKey() + " time: " + entry.getValue());
                        }
                    }
                    System.out.println("Select a train by typing 1,2,3...");
                    int selectTrain = new Scanner(System.in).nextInt();
                    if(selectTrain > trains.size()){
                        System.out.println("Invalid train id");
                        break;
                    }

                    trainSelectedForBooking = trains.get(selectTrain - 1);
                    System.out.println("You have selected train id: " + trainSelectedForBooking.getTrainId());
                    break;
                case 5:
                    System.out.println("Select a seat out of these seats");
                    List<List<Integer>> seats;
                    try {
                        seats = userBookingService.fetchSeats(trainSelectedForBooking);
                        if (seats == null || seats.size() == 0) {
                            System.out.println("No seats available");
                            break;
                        }
                    } catch (Exception e) {
                        System.out.println("Error fetching seats: " + e.getMessage());
                        break;
                    }
                    for (List<Integer> row : seats) {
                        for (Integer val : row) {
                            System.out.print(val + " ");
                        }
                        System.out.println();
                    }
                    System.out.println("Select the seat by typing the row and column (0-4)");
                    try {
                        System.out.println("Enter the row");
                        int row = scanner.nextInt();
                        System.out.println("Enter the column");
                        int col = scanner.nextInt();
                        if (row < 0 || row >= seats.size() || col < 0 || col >= seats.get(0).size()) {
                            System.out.println("Invalid row or column. Must be between 0 and 4.");
                            break;
                        }
                        System.out.println("Booking your seat....");
                        Boolean booked = userBookingService.bookTrainSeat(trainSelectedForBooking, row, col);
                        if (booked) {
                            System.out.println("Booked! Enjoy your journey");
                        } else {
                            System.out.println("Can't book this seat");
                        }
                    } catch (java.util.InputMismatchException e) {
                        System.out.println("Invalid input. Please enter numbers for row and column.");
                        scanner.nextLine(); // Clear invalid input
                    } catch (Exception e) {
                        System.out.println("Error booking seat: " + e.getMessage());
                    }
                    break;
                default:
                    break;
            }
        }
    }
}