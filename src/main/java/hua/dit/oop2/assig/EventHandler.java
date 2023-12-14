package hua.dit.oop2.assig;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class EventHandler {

    private EventManager eventManager;

    public EventHandler(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void addEvent(String type, String title, String description, LocalDate date, LocalTime startTime, int durationMinutes, LocalDateTime deadline) {
        if (type.equals("appointment")) {
            Appointment appointment = new Appointment(title, description, date, startTime, Duration.ofMinutes(durationMinutes));
            eventManager.addEvent(appointment);
        } else if (type.equals("task")) {
            Task task = new Task(title, description, date, deadline);
            eventManager.addEvent(task);
        }
    }

    public void editEvent(String titleToEdit, String newType, String newTitle, String newDescription, LocalDate newDate, LocalTime newStartTime, int newDurationMinutes, LocalDateTime newDeadline) {
        Event event = eventManager.findEventByTitle(titleToEdit);
        if (event != null) {
            eventManager.deleteEvent(titleToEdit); // Remove the old event
            addEvent(newType, newTitle, newDescription, newDate, newStartTime, newDurationMinutes, newDeadline); // Add the updated event
        }
    }

    public void viewEventsForDay(LocalDate date) {
        List<Event> events = eventManager.getEventsForDay(date);
        displayEvents(events);
    }

    public void viewEventsForWeek(LocalDate date) {
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        List<Event> events = eventManager.getEventsForPeriod(startOfWeek, endOfWeek);
        displayEvents(events);
    }

    public void viewEventsForMonth(LocalDate date) {
        LocalDate startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());
        List<Event> events = eventManager.getEventsForPeriod(startOfMonth, endOfMonth);
        displayEvents(events);
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
}
