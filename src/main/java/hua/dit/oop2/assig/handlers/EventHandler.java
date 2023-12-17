package hua.dit.oop2.assig.handlers;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Scanner;
import gr.hua.dit.oop2.calendar.TimeService;
import gr.hua.dit.oop2.calendar.TimeTeller;
import hua.dit.oop2.assig.core.*;

public class EventHandler {

    private EventManager eventManager;
    private Scanner scanner;
    private static final TimeTeller teller = TimeService.getTeller();

    public EventHandler(EventManager eventManager) {
        this.eventManager = eventManager;
        this.scanner = new Scanner(System.in);
    }

    public void addEventsFromUserInput() {
        boolean moreEvents = true;

        while (moreEvents) {
            System.out.println("Enter event type (appointment/task/event):");
            String type = scanner.nextLine().toLowerCase();

            Event event = null;
            switch (type) {
                case "appointment":
                    event = createAppointmentFromUserInput();
                    break;
                case "task":
                    event = createTaskFromUserInput();
                    break;
                case "event":
                    event = createEventFromUserInput();
                    break;
                default:
                    System.out.println("Invalid event type. Please enter 'appointment', 'task', or 'event'.");
                    continue;
            }

            if (event != null) {
                eventManager.addEvent(event);
            }

            System.out.println("Do you want to add another event? (yes/no):");
            moreEvents = scanner.nextLine().trim().equalsIgnoreCase("yes");
        }
    }

    private Appointment createAppointmentFromUserInput() {
        System.out.println("Enter title:");
        String title = scanner.nextLine();

        System.out.println("Enter description:");
        String description = scanner.nextLine();

        LocalDate date = readDateFromUser("Enter date (DD-MM-YYYY):");
        LocalTime startTime = readTimeFromUser("Enter start time (HH:mm):");

        System.out.println("Enter duration in minutes:");
        int durationMinutes = Integer.parseInt(scanner.nextLine());
        Duration duration = Duration.ofMinutes(durationMinutes);

        return new Appointment(title, description, date, startTime, duration);
    }

    private Event createEventFromUserInput() {
        System.out.println("Enter title:");
        String title = scanner.nextLine();

        System.out.println("Enter description:");
        String description = scanner.nextLine();

        LocalDate date = readDateFromUser("Enter date (DD-MM-YYYY):");

        System.out.println("Is this an all-day event? (yes/no):");
        boolean isAllDay = scanner.nextLine().trim().equalsIgnoreCase("yes");

        if (isAllDay) {
            return new AllDayEvent(title, description, date);
        } else {
            System.out.println("Does this event have a specific start time? (yes/no):");
            boolean hasStartTime = scanner.nextLine().trim().equalsIgnoreCase("yes");
            if (hasStartTime) {
                LocalTime startTime = readTimeFromUser("Enter start time (HH:mm):");
                return new Event(title, description, date, startTime);
            } else {
                return new Event(title, description, date); // No specific time
            }
        }
    }

    private Task createTaskFromUserInput() {
        System.out.println("Enter title:");
        String title = scanner.nextLine();

        System.out.println("Enter description:");
        String description = scanner.nextLine();

        LocalDate date = readDateFromUser("Enter date (DD-MM-YYYY):");

        LocalDateTime deadline = readDateTimeFromUser("Enter deadline date and time (DD-MM-YYYY HH:mm):");

        return new Task(title, description, date, deadline);
    }

    private LocalDate readDateFromUser(String message) {
        while (true) {
            System.out.println(message);
            try {
                return LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please try again.");
            }
        }
    }

    private LocalTime readTimeFromUser(String message) {
        while (true) {
            System.out.println(message);
            try {
                return LocalTime.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format. Please try again.");
            }
        }
    }

    private LocalDateTime readDateTimeFromUser(String message) {
        while (true) {
            System.out.println(message);
            try {
                return LocalDateTime.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date and time format. Please try again.");
            }
        }
    }

    public void viewEvents(String periodType, LocalDate date) {
        LocalDate startDate, endDate;

        switch (periodType.toLowerCase()) {
            case "day":
                startDate = endDate = date;
                break;
            case "week":
                startDate = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                endDate = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                break;
            case "month":
                startDate = date.with(TemporalAdjusters.firstDayOfMonth());
                endDate = date.with(TemporalAdjusters.lastDayOfMonth());
                break;
            default:
                startDate = endDate = date; // Default to a single day
                break;
        }

        List<Event> events = eventManager.getEventsForPeriod(startDate, endDate);
        displayEvents(events);
    }

    public void viewPendingTasks() {
        List<Event> pendingTasks = eventManager.getPendingTasks();
        displayEvents(pendingTasks);
    }

    public void viewPastDueTasks() {
        List<Event> pastDueTasks = eventManager.getPastDueTasks();
        displayEvents(pastDueTasks);
    }

    private void displayEvents(List<Event> events) {
        if (events.isEmpty()) {
            System.out.println("No events to display.");
            return;
        }
        for (Event event : events) {
            displayEventDetails(event);
        }
    }

    private void displayEventDetails(Event event) {
        String title = event.getTitle().isEmpty() ? "No title" : event.getTitle();
        String description = event.getDescription().isEmpty() ? "No description" : event.getDescription();

        System.out.println("Title: " + title);
        System.out.println("Description: " + description);
        System.out.println("Date: " + event.getDate());

        if (event instanceof Appointment) {
            Appointment appointment = (Appointment) event;
            System.out.println("Start Time: " + appointment.getStartTime());
            System.out.println("Duration: " + formatDuration(appointment.getDuration()));
        } else if (event instanceof Task) {
            Task task = (Task) event;
            System.out.println("Deadline: " + task.getDeadline());
            System.out.println("Completed: " + task.isCompleted());
        }
    }

    private String formatDuration(Duration duration) {
        if (duration == null) {
            return "Duration not specified";
        }
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return String.format("%d hours and %d minutes", hours, minutes);
    }
}
