package hua.dit.oop2.assig;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Scanner;

public class EventHandler {

    private EventManager eventManager;

    public EventHandler(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void addEventsFromUserInput() {
        Scanner scanner = new Scanner(System.in);
        boolean moreEvents = true;

        while (moreEvents) {

            System.out.println("Enter event type (appointment/task/event):");
            String type = scanner.nextLine().toLowerCase();

            String title, description;
            LocalDate date;
            LocalTime startTime = null;
            Duration duration = null;
            boolean isAllDay = false;

            System.out.println("Enter title:");
            title = scanner.nextLine().trim();

            System.out.println("Enter description:");
            description = scanner.nextLine().trim();

            date = null;
            while (date == null) {
                System.out.println("Enter date (DD-MM-YYYY):");
                try {
                    date = LocalDate.parse(scanner.nextLine().trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Please try again.");
                }
            }

            if (type.equals("event")) {
                System.out.println("Is this an all-day event? (yes/no):");
                isAllDay = scanner.nextLine().trim().equalsIgnoreCase("yes");
            }

            if ((type.equals("appointment") || (type.equals("event") && !isAllDay)) && startTime == null) {
                while (startTime == null) {
                    System.out.println("Enter start time (HH:mm):");
                    try {
                        startTime = LocalTime.parse(scanner.nextLine().trim(), DateTimeFormatter.ofPattern("HH:mm"));
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid time format. Please try again.");
                    }
                }

                System.out.println("Enter duration in minutes:");
                duration = Duration.ofMinutes(scanner.nextInt());
                scanner.nextLine(); // consume the remaining newline
            }

            Event event = null;
            if (type.equals("event") && isAllDay) {
                event = new AllDayEvent(title, description, date);
            } else if (type.equals("appointment") || (type.equals("event") && !isAllDay)) {
                event = new Appointment(title, description, date, startTime, duration);
            } else if (type.equals("task")) {
                // Add your existing logic for creating a Task object here
            }

            if (event != null) {
                eventManager.addEvent(event);
            }

            System.out.println("Do you want to add another event? (yes/no):");
            moreEvents = scanner.nextLine().trim().equalsIgnoreCase("yes");
        }
    }





    private LocalDate parseDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(dateString, formatter);
    }

    private LocalTime parseTime(String timeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(timeString, formatter);
    }

    private LocalDateTime parseDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return LocalDateTime.parse(dateTimeString, formatter);
    }



    private void addEvent(String type, String title, String description, LocalDate date, LocalTime startTime, int durationMinutes, LocalDateTime deadline) {
        if ("appointment".equals(type)) {
            Appointment appointment = new Appointment(title, description, date, startTime, Duration.ofMinutes(durationMinutes));
            eventManager.addEvent(appointment);
        } else if ("task".equals(type)) {
            Task task = new Task(title, description, date, deadline);
            eventManager.addEvent(task);
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
        System.out.println("Title: " + event.getTitle());
        System.out.println("Description: " + event.getDescription());
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
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return String.format("%d hours and %d minutes", hours, minutes);
    }


    public void listAllEvents() {
        List<Event> allEvents = eventManager.getAllEvents();
        if (allEvents.isEmpty()) {
            System.out.println("No events to display.");
            return;
        }
        for (Event event : allEvents) {
            displayEventDetails(event);
        }
    }

    public void editEvent(String titleToEdit, String newType, String newTitle, String newDescription, LocalDate newDate, LocalTime newStartTime, int newDurationMinutes, LocalDateTime newDeadline) {
        Event event = eventManager.findEventByTitle(titleToEdit);
        if (event != null) {
            eventManager.deleteEvent(titleToEdit); // Remove the old event
            addEvent(newType, newTitle, newDescription, newDate, newStartTime, newDurationMinutes, newDeadline); // Add the updated event
        }
    }

    public void deleteEvent(String title) {
        Event event = eventManager.findEventByTitle(title);
        if (event != null) {
            eventManager.deleteEvent(title);
            System.out.println("Event deleted successfully.");
        } else {
            System.out.println("Event not found.");
        }
    }

}
