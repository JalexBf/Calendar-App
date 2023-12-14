package hua.dit.oop2.assig;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static EventManager manager = new EventManager();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("Welcome to the Event Manager");
            System.out.println("1. Add Event");
            System.out.println("2. Edit Event");
            System.out.println("3. View Event");
            System.out.println("4. Delete Event");
            System.out.println("5. List All Events");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();
            scanner.nextLine(); // consume the newline

            switch (option) {
                case 1:
                    addEvent();
                    break;
                case 2:
                    editEvent();
                    break;
                case 3:
                    deleteEvent();
                    break;
                case 4:
                    deleteEvent();
                    break;
                case 5:
                    listAllEvents();
                    break;
                case 6:
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid option, please choose again.");
            }
        }
    }

    private static void addEvent() {
        System.out.println("Enter event type (appointment/task):");
        String type = scanner.nextLine().trim().toLowerCase();

        if (!type.equals("appointment") && !type.equals("task")) {
            System.out.println("Invalid event type. Please enter 'appointment' or 'task'.");
            return;
        }

        System.out.println("Enter title:");
        String title = scanner.nextLine();
        if (title.isEmpty()) {
            System.out.println("Title cannot be empty.");
            return;
        }

        System.out.println("Enter description:");
        String description = scanner.nextLine();

        LocalDate date = readDate();

        if (type.equals("appointment")) {
            LocalTime startTime = readTime(); // Assuming readTime() is a similar method to readDate()
            System.out.println("Enter duration in minutes:");
            long durationMinutes = scanner.nextLong();
            scanner.nextLine(); // consume the newline

            Appointment appointment = new Appointment(title, description, date, startTime, Duration.ofMinutes(durationMinutes));
            manager.addEvent(appointment);
        } else {
            LocalDateTime deadline = readDateTime(); // Assuming readDateTime() is a method for LocalDateTime
            Task task = new Task(title, description, date, deadline);
            manager.addEvent(task);
        }

        System.out.println("Event added successfully.");
    }


    private static void editEvent() {
        listAllEvents();
        System.out.println("Enter the title of the event to edit:");
        String title = scanner.nextLine();

        Event event = manager.findEventByTitle(title);
        if (event == null) {
            System.out.println("Event not found.");
            return;
        }

        System.out.println("Enter new details for the event:");
        addEvent();
        manager.deleteEvent(title);
        System.out.println("Event edited successfully.");
    }


    private static void viewEvent() {
        System.out.println("Choose an option to view events:");
        System.out.println("1. View events for a specific day");
        System.out.println("2. View events for a specific week");
        System.out.println("3. View events for a specific month");
        System.out.print("Choose an option: ");

        int option = scanner.nextInt();
        scanner.nextLine(); // consume the newline

        switch (option) {
            case 1:
                viewEventsForDay();
                break;
            case 2:
                viewEventsForWeek();
                break;
            case 3:
                viewEventsForMonth();
                break;
            default:
                System.out.println("Invalid option, please choose again.");
        }
    }

    private static void viewEventsForDay() {
        System.out.println("Viewing events for a specific day.");
        LocalDate date = readDate();
        List<Event> events = manager.getEventsForDay(date);
        displayEvents(events);
    }


    private static void viewEventsForWeek() {
        System.out.println("Viewing events for a specific week.");
        LocalDate date = readDate();
        List<Event> events = manager.getEventsForWeek(date);
        displayEvents(events);
    }


    private static void viewEventsForMonth() {
        System.out.println("Viewing events for a specific month.");
        LocalDate date = readDate();
        List<Event> events = manager.getEventsForMonth(date);
        displayEvents(events);
    }


    private static void displayEvents(List<Event> events) {
        if (events.isEmpty()) {
            System.out.println("No events found.");
            return;
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (Event event : events) {
            System.out.println("-----------------------------------");
            System.out.println("Title: " + event.getTitle());
            System.out.println("Description: " + event.getDescription());
            System.out.println("Date: " + event.getDate().format(dateFormatter));

            if (event instanceof Appointment) {
                Appointment appointment = (Appointment) event;
                System.out.println("Type: Appointment");
                System.out.println("Start Time: " + appointment.getStartTime().format(timeFormatter));
                System.out.println("Duration: " + formatDuration(appointment.getDuration()));
            } else if (event instanceof Task) {
                Task task = (Task) event;
                System.out.println("Type: Task");
                System.out.println("Deadline: " + task.getDeadline().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                System.out.println("Completed: " + (task.isCompleted() ? "Yes" : "No"));
            }
            System.out.println("-----------------------------------");
        }
    }


    private static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return String.format("%d hours and %d minutes", hours, minutes);
    }


    private static void deleteEvent() {
        System.out.println("Enter the title of the event to delete:");
        String title = scanner.nextLine();

        Event event = manager.findEventByTitle(title);
        if (event != null) {
            manager.deleteEvent(title);
            System.out.println("Event deleted successfully.");
        } else {
            System.out.println("Event not found.");
        }
    }


    private static void listAllEvents() {
        List<Event> allEvents = manager.getAllEvents();
        if (allEvents.isEmpty()) {
            System.out.println("No events to display.");
            return;
        }
        for (Event event : allEvents) {
            System.out.println("Title: " + event.getTitle() + ", Description: " + event.getDescription() + ", Date: " + event.getDate());

            if (event instanceof Appointment) {
                Appointment appointment = (Appointment) event;
                System.out.println("Start Time: " + appointment.getStartTime() + ", Duration: " + appointment.getDuration());
            } else if (event instanceof Task) {
                Task task = (Task) event;
                System.out.println("Deadline: " + task.getDeadline() + ", Completed: " + task.isCompleted());
            }
        }
    }

    private static LocalDate readDate() {
        while (true) {
            try {
                System.out.println("Enter date (YYYY-MM-DD):");
                return LocalDate.parse(scanner.nextLine());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please try again.");
            }
        }
    }

    private static LocalTime readTime() {
        while (true) {
            try {
                System.out.println("Enter start time (HH:MM):");
                String input = scanner.nextLine();
                return LocalTime.parse(input); // Parses the time input
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format. Please try again using HH:MM format.");
            }
        }
    }

    private static LocalDateTime readDateTime() {
        while (true) {
            try {
                System.out.println("Enter deadline (YYYY-MM-DDTHH:MM):");
                String input = scanner.nextLine();
                return LocalDateTime.parse(input); // Parses the datetime input
            } catch (DateTimeParseException e) {
                System.out.println("Invalid datetime format. Please try again using YYYY-MM-DDTHH:MM format.");
            }
        }
    }

}
