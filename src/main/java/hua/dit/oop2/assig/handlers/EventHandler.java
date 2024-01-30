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

    public EventHandler() {
        this.eventManager = EventManager.getInstance(); // Use singleton instance
        this.scanner = new Scanner(System.in);
    }


    public void addEventsFromUserInput() {
        boolean moreEvents = true;

        while (moreEvents) {
            System.out.println("Enter event type (appointment/task/event):");
            String type = scanner.nextLine().trim().toLowerCase();

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
                if (!isDuplicateEvent(event)) {
                    eventManager.addEvent(event);
                } else {
                    System.out.println("An event with the same UUID already exists. Please try a different event.");
                }
            }
            moreEvents = askForMoreEvents();
        }
    }


    private Appointment createAppointmentFromUserInput() {
        String title = readTitleFromUser();
        String description = readDescriptionFromUser();
        LocalDate date = readDateFromUser("Enter date (DD-MM-YYYY):");
        LocalTime startTime = readTimeFromUser("Enter start time (HH:mm):");

        int durationMinutes;
        while (true) {
            try {
                System.out.println("Enter duration in minutes:");
                durationMinutes = Integer.parseInt(scanner.nextLine().trim());
                if (durationMinutes >= 0) {
                    break;
                } else {
                    System.out.println("Duration must be a non-negative number. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number for duration.");
            }
        }

        LocalTime endTime = startTime.plus(Duration.ofMinutes(durationMinutes));
        return new Appointment(title, description, date, startTime, endTime);
    }


    private Event createEventFromUserInput() {
        String title = readTitleFromUser();
        String description = readDescriptionFromUser();

        LocalDate date;
        LocalTime time;
        LocalDateTime eventDateTime;

        // Get and validate the date
        while (true) {
            date = readDateFromUser("Enter date (DD-MM-YYYY):");
            if (!date.isBefore(LocalDate.now())) {
                break;
            }
            System.out.println("Sorry, you cannot set a date in the past. Please try again.");
        }

        System.out.println("Is this an all-day event? (yes/no):");
        boolean isAllDay = scanner.nextLine().trim().equalsIgnoreCase("yes");

        if (isAllDay) {
            return new Event(title, description, date);
        } else {
            // Get and validate the time
            while (true) {
                time = readTimeFromUser("Enter start time (HH:mm):");
                eventDateTime = LocalDateTime.of(date, time);

                if (!eventDateTime.isBefore(LocalDateTime.now())) {
                    return new Event(title, description, date, time);
                } else {
                    System.out.println("Sorry, you cannot set a time in the past. Please try again.");
                }
            }
        }
    }



    private Task createTaskFromUserInput() {
        String title = readTitleFromUser();
        String description = readDescriptionFromUser();

        LocalDateTime deadline = readFutureDateTimeFromUser("Enter deadline date (DD-MM-YYYY):", "Enter deadline time (HH:mm):");

        System.out.println("Is the task completed? (yes/no):");
        boolean isCompleted = scanner.nextLine().trim().equalsIgnoreCase("yes");

        Task newTask = new Task(title, description, deadline);
        newTask.setCompleted(isCompleted);
        return newTask;
    }


    private LocalDate readDateFromUser(String message) {
        while (true) {
            System.out.println(message);
            try {
                return LocalDate.parse(scanner.nextLine().trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please try again.");
            }
        }
    }


    private LocalTime readTimeFromUser(String message) {
        while (true) {
            System.out.println(message);
            try {
                return LocalTime.parse(scanner.nextLine().trim(), DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format. Please try again.");
            }
        }
    }


    private String readTitleFromUser() {
        String title;
        final int maxTitleLength = 100;
        do {
            System.out.println("Enter title (up to " + maxTitleLength + " characters):");
            title = scanner.nextLine().trim();
            if (title.length() > maxTitleLength) {
                System.out.println("Title is too long. Please enter a shorter title.");
            }
        } while (title.isEmpty() || title.length() > maxTitleLength);
        return title;
    }


    private String readDescriptionFromUser() {
        String description;
        final int maxDescriptionLength = 500;
        System.out.println("Enter description (up to " + maxDescriptionLength + " characters, can be empty):");
        description = scanner.nextLine().trim();
        while (description.length() > maxDescriptionLength) {
            System.out.println("Description is too long. Please enter a shorter description (up to " + maxDescriptionLength + " characters):");
            description = scanner.nextLine().trim();
        }
        return description;
    }


    private LocalDateTime readFutureDateTimeFromUser(String datePrompt, String timePrompt) {
        LocalDate date;
        LocalTime time;
        LocalDateTime dateTime;
        do {
            date = readDateFromUser(datePrompt);
            time = readTimeFromUser(timePrompt);
            dateTime = LocalDateTime.of(date, time);
            if (dateTime.isBefore(LocalDateTime.now())) {
                System.out.println("Sorry, you cannot set a date and time in the past. Please try again.");
            }
        } while (dateTime.isBefore(LocalDateTime.now()));
        return dateTime;
    }


    private LocalDateTime readDateTimeFromUser(String message) {
        while (true) {
            System.out.println(message);
            try {
                return LocalDateTime.parse(scanner.nextLine().trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date and time format. Please try again.");
            }
        }
    }


    public void viewEvents(String periodType, LocalDate referenceDate) {
        LocalDate startDate = referenceDate, endDate = referenceDate;

        switch (periodType.toLowerCase()) {
            case "day":
                // startDate and endDate are already set to referenceDate
                break;
            case "week":
                startDate = referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                endDate = referenceDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                break;
            case "month":
                startDate = referenceDate.with(TemporalAdjusters.firstDayOfMonth());
                endDate = referenceDate.with(TemporalAdjusters.lastDayOfMonth());
                break;
            case "pastday":
                startDate = endDate = referenceDate.minusDays(1);
                break;
            case "pastweek":
                startDate = referenceDate.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                endDate = startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                break;
            case "pastmonth":
                startDate = referenceDate.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
                endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
                break;
            case "nextday":
                startDate = endDate = referenceDate.plusDays(1);
                break;
            case "nextweek":
                startDate = referenceDate.plusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                endDate = startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                break;
            case "todo":
                viewPendingTasks();
                return; // Skip the default event display logic as viewPendingTasks() handles it
            case "due":
                viewPastDueTasks();
                return; // Skip the default event display logic as viewPastDueTasks() handles it
            default:
                System.out.println("Unknown period type. Showing events for today.");
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
            System.out.println("End Time: " + appointment.getEndTime());
            System.out.println("Duration: " + formatDurationBetween(appointment.getStartTime(), appointment.getEndTime()));
        } else if (event instanceof Task) {
            Task task = (Task) event;
            System.out.println("Deadline: " + task.getDeadline());
            System.out.println("Completed: " + task.isCompleted());
        }
    }

    private String formatDurationBetween(LocalTime startTime, LocalTime endTime) {
        Duration duration = Duration.between(startTime, endTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%d hours and %d minutes", hours, minutes);
    }


    private boolean askForMoreEvents() {
        String input;
        do {
            System.out.println("Do you want to add another event? (yes/no):");
            input = scanner.nextLine().trim().toLowerCase();
            if (!input.equals("yes") && !input.equals("no")) {
                System.out.println("Invalid input. Please enter 'yes' or 'no'.");
            }
        } while (!input.equals("yes") && !input.equals("no"));
        return input.equals("yes");
    }


    private boolean isDuplicateEvent(Event event) {
        return eventManager.getAllEvents().stream()
                .anyMatch(existingEvent -> existingEvent.getUuid().equals(event.getUuid()));
    }


    public void updateEventFromUserInput() {
        List<Event> events = eventManager.getAllEvents();
        if (events.isEmpty()) {
            System.out.println("No events available to update.");
            return;
        }
        displayEventsWithIndices(events);

        int index = getUserInputForIndex(events);
        if (index >= 0 && index < events.size()) {
            Event eventToUpdate = events.get(index);
            String uuid = eventToUpdate.getUuid();

            System.out.println("Enter new details for the event.");
            System.out.println("Current Title: " + eventToUpdate.getTitle());
            String title = readTitleFromUser();
            System.out.println("Current Description: " + eventToUpdate.getDescription());
            String description = readDescriptionFromUser();
            LocalDate date = readDateFromUser("Enter new date (DD-MM-YYYY):");

            if (eventToUpdate instanceof Appointment) {
                LocalTime startTime = readTimeFromUser("Enter new start time (HH:mm):");

                int durationMinutes;
                while (true) {
                    try {
                        System.out.println("Enter new duration in minutes:");
                        durationMinutes = Integer.parseInt(scanner.nextLine().trim());
                        if (durationMinutes >= 0) {
                            break;
                        } else {
                            System.out.println("Duration must be a non-negative number. Please try again.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid number for duration.");
                    }
                }

                LocalTime endTime = startTime.plusMinutes(durationMinutes);

                Appointment updatedAppointment = new Appointment(title, description, date, startTime, endTime);
                updatedAppointment.setUuid(uuid);
                eventManager.updateEvent(uuid, updatedAppointment);
            } else if (eventToUpdate instanceof Task) {
                LocalTime time = readTimeFromUser("Enter new deadline time (HH:mm):");
                LocalDateTime deadline = LocalDateTime.of(date, time);

                boolean isCompleted = askYesNoQuestion("Is the task completed? (yes/no):");

                Task updatedTask = new Task(title, description, deadline);
                updatedTask.setCompleted(isCompleted);
                updatedTask.setUuid(uuid);
                eventManager.updateEvent(uuid, updatedTask);
            } else if (eventToUpdate instanceof AllDayEvent) {
                AllDayEvent updatedEvent = new AllDayEvent(title, description, date);
                updatedEvent.setUuid(uuid);
                eventManager.updateEvent(uuid, updatedEvent);
            } else {
                LocalTime time = readTimeFromUser("Enter new start time (HH:mm):");
                Event updatedEvent = new Event(title, description, date, time);
                updatedEvent.setUuid(uuid);
                eventManager.updateEvent(uuid, updatedEvent);
            }

            System.out.println("Event updated successfully.");
        }
    }


    private boolean askYesNoQuestion(String question) {
        System.out.println(question);
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("yes");
    }


    private void displayEventsWithIndices(List<Event> events) {
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            System.out.println("[" + i + "] " + event.getTitle() + " - " + event.getDate() + " " + event.getStartTime() + " - " + event.getDescription());
        }
    }


    private int getUserInputForIndex(List<Event> events) {
        int index = -1;
        System.out.print("Enter the number of the event you want to update: "); // Prompt moved outside the loop

        while (index == -1) {
            try {
                String input = scanner.nextLine();
                index = Integer.parseInt(input);

                if (index < 0 || index >= events.size()) {
                    System.out.println("Invalid selection. Please enter a number between 0 and " + (events.size() - 1) + ".");
                    index = -1;  // Reset index to keep the loop going
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                System.out.print("Enter the number of the event you want to update: "); // Prompt for retry
            }
        }
        return index;
    }
}
