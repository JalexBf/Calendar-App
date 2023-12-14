import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private static EventManager manager = new EventManager();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No arguments provided. Exiting...");
            System.exit(0);
        }

        String firstArg = args[0];

        if (args.length == 2) {
            String icalFilePath = args[1];
            processDisplayCommand(firstArg, icalFilePath);
        } else if (args.length == 1) {
            String icalFilePath = args[0];
            updateCalendar(icalFilePath);
        } else {
            System.out.println("Invalid number of arguments. Exiting...");
            System.exit(1);
        }
    }


    private static void processDisplayCommand(String command, String icalFilePath) {
        List<Event> events = readFromICalFile(icalFilePath); // Implement this method to read events from iCal file
        LocalDate currentDate = LocalDate.now();

        switch (command.toLowerCase()) {
            case "day":
                displayEvents(events.stream()
                        .filter(e -> e.getDate().isEqual(currentDate))
                        .collect(Collectors.toList()));
                break;
            case "week":
                displayEventsForPeriod(events, currentDate, ChronoUnit.WEEKS);
                break;
            case "month":
                displayEventsForPeriod(events, currentDate, ChronoUnit.MONTHS);
                break;
            case "pastday":
                displayEvents(events.stream()
                        .filter(e -> e.getDate().isEqual(currentDate.minusDays(1)))
                        .collect(Collectors.toList()));
                break;
            case "pastweek":
                displayEventsForPeriod(events, currentDate.minusWeeks(1), ChronoUnit.WEEKS);
                break;
            case "pastmonth":
                displayEventsForPeriod(events, currentDate.minusMonths(1), ChronoUnit.MONTHS);
                break;
            case "todo":
                displayEvents(events.stream()
                        .filter(e -> e instanceof Task && !((Task)e).isCompleted())
                        .collect(Collectors.toList()));
                break;
            case "due":
                displayEvents(events.stream()
                        .filter(e -> e instanceof Task && ((Task)e).isPastDue())
                        .collect(Collectors.toList()));
                break;
            default:
                System.out.println("Unknown display command.");
        }
    }


    private static List<Event> readFromICalFile(String filePath) {
        List<Event> events = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            String title = "", description = "";
            LocalDate date = null;
            LocalTime startTime = null;
            Duration duration = null;
            LocalDateTime deadline = null;
            LocalDateTime startDateTime = null; // For calculating duration
            boolean isCompleted = false; // Default value for tasks

            for (String line : lines) {
                if (line.startsWith("BEGIN:VEVENT")) {
                    title = ""; description = ""; date = null;
                    startTime = null; duration = null; deadline = null;
                    isCompleted = false;
                } else if (line.startsWith("END:VEVENT")) {
                    if (startTime != null) {
                        events.add(new Appointment(title, description, date, startTime, duration));
                    } else if (deadline != null) {
                        Task task = new Task(title, description, date, deadline);
                        task.setCompleted(isCompleted);
                        events.add(task);
                    }
                } else {
                    if (line.startsWith("SUMMARY:")) {
                        title = line.substring(8);
                    } else if (line.startsWith("DESCRIPTION:")) {
                        description = line.substring(13);
                    } else if (line.startsWith("DTSTART:")) {
                        String dateTimeString = line.substring(8);
                        startDateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));
                        date = startDateTime.toLocalDate();
                        startTime = startDateTime.toLocalTime();
                    } else if (line.startsWith("DTEND:")) {
                        String dateTimeString = line.substring(6);
                        LocalDateTime endDateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));
                        duration = Duration.between(startDateTime, endDateTime);
                    } else if (line.startsWith("DUE:")) {
                        String deadlineString = line.substring(4);
                        deadline = LocalDateTime.parse(deadlineString, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));
                    }
                    // Add more parsing as per your Event structure and iCal file format
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading iCal file: " + e.getMessage());
        }
        return events;
    }



    private static void writeToICalFile(String filePath, List<Event> events) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("BEGIN:VCALENDAR\n");
            writer.write("VERSION:2.0\n");
            writer.write("PRODID:-//Your Company//Your Product//EN\n");

            for (Event event : events) {
                String icalEvent = eventToICalFormat(event);
                writer.write(icalEvent);
            }

            writer.write("END:VCALENDAR\n");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the iCal file: " + e.getMessage());
        }
    }

    private static String eventToICalFormat(Event event) {
        StringBuilder builder = new StringBuilder();

        builder.append("BEGIN:VEVENT\n");
        builder.append("SUMMARY:").append(event.getTitle()).append("\n");
        builder.append("DESCRIPTION:").append(event.getDescription()).append("\n");
        builder.append("DTSTART:").append(convertToICalDateTimeFormat(event.getDate())).append("\n");

        // Handle end time or duration for Appointment
        if (event instanceof Appointment) {
            Appointment appointment = (Appointment) event;
            LocalDateTime endDateTime = LocalDateTime.of(appointment.getDate(), appointment.getStartTime()).plus(appointment.getDuration());
            builder.append("DTEND:").append(convertToICalDateTimeFormat(endDateTime.toLocalDate())).append("\n");
        }

        // Handle deadline for Task
        if (event instanceof Task) {
            Task task = (Task) event;
            builder.append("DUE:").append(convertToICalDateTimeFormat(task.getDeadline().toLocalDate())).append("\n");
        }

        builder.append("END:VEVENT\n");
        return builder.toString();
    }

    private static String convertToICalDateTimeFormat(LocalDate date) {
        return DateTimeFormatter.ofPattern("yyyyMMdd").format(date) + "T" + DateTimeFormatter.ofPattern("HHmmss").format(LocalTime.MIDNIGHT) + "Z";
    }



    private static void updateCalendar(String icalFilePath) {
        List<Event> events = readFromICalFile(icalFilePath); // Read existing events
        Scanner scanner = new Scanner(System.in);
        boolean moreEvents = true;

        while (moreEvents) {
            System.out.println("Adding a new event.");

            System.out.print("Enter event type (appointment/task): ");
            String eventType = scanner.nextLine().toLowerCase();

            System.out.print("Enter title: ");
            String title = scanner.nextLine();

            System.out.print("Enter description: ");
            String description = scanner.nextLine();

            System.out.print("Enter date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(scanner.nextLine());

            Event event;
            if ("appointment".equals(eventType)) {
                System.out.print("Enter start time (HH:MM): ");
                LocalTime startTime = LocalTime.parse(scanner.nextLine());

                System.out.print("Enter duration in minutes: ");
                long durationMinutes = scanner.nextLong();
                scanner.nextLine(); // consume the newline

                event = new Appointment(title, description, date, startTime, Duration.ofMinutes(durationMinutes));
            } else { // assuming task
                System.out.print("Enter deadline (YYYY-MM-DDTHH:MM): ");
                LocalDateTime deadline = LocalDateTime.parse(scanner.nextLine());
                event = new Task(title, description, date, deadline);
            }

            events.add(event);

            System.out.print("Do you want to add another event? (yes/no): ");
            String response = scanner.nextLine();
            moreEvents = "yes".equalsIgnoreCase(response);
        }

        writeToICalFile(icalFilePath, events); // Save the new list of events back to the file
        System.out.println("Calendar updated successfully.");
    }


    private static void displayEventsForPeriod(List<Event> events, LocalDate referenceDate, ChronoUnit unit) {
        LocalDate startDate = referenceDate.with(unit == ChronoUnit.WEEKS ? TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY) : TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = referenceDate.with(unit == ChronoUnit.WEEKS ? TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY) : TemporalAdjusters.lastDayOfMonth());

        displayEvents(events.stream()
                .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate))
                .collect(Collectors.toList()));
    }

    private static void displayEvents(List<Event> filteredEvents) {
        if (filteredEvents.isEmpty()) {
            System.out.println("No events to display.");
            return;
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (Event event : filteredEvents) {
            System.out.println("----------------------------");
            System.out.println("Title: " + event.getTitle());
            System.out.println("Description: " + event.getDescription());
            System.out.println("Date: " + event.getDate().format(dateFormatter));

            if (event instanceof Appointment) {
                Appointment appointment = (Appointment) event;
                System.out.println("Type: Appointment");
                System.out.println("Start Time: " + appointment.getStartTime().format(timeFormatter));
                System.out.println("Duration: " + formatDuration(appointment.getDuration())); // Corrected this line
            } else if (event instanceof Task) {
                Task task = (Task) event;
                System.out.println("Type: Task");
                System.out.println("Deadline: " + task.getDeadline().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                System.out.println("Completed: " + (task.isCompleted() ? "Yes" : "No"));
            }
            System.out.println("----------------------------");
        }
    }

    private static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return String.format("%d hours and %d minutes", hours, minutes);
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

