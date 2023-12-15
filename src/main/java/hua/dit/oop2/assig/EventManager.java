package hua.dit.oop2.assig;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EventManager {
    private List<Event> events;

    public EventManager() {
        this.events = new ArrayList<>();
    }

    // Add event to the collection
    public void addEvent(Event event) {
        this.events.add(event);
    }

    // Delete an event from the collection
    public boolean deleteEvent(String title) {
        return events.removeIf(event -> event.getTitle().equalsIgnoreCase(title));
    }


    // Method to find an event by its title
    public Event findEventByTitle(String title) {
        for (Event event : events) {
            if (event.getTitle().equals(title)) {
                return event;
            }
        }
        return null; // Return null if event not found
    }


    // Edit event in the collection
    public void editEvent(String title, Event newEventDetails) {
        // Find event by title or another unique identifier
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            if (event.getTitle().equals(title)) {
                events.set(i, newEventDetails); // Replace with new details
                break;
            }
        }
    }


    // List all events
    public List<Event> getAllEvents() {
        return new ArrayList<>(events); // Return a copy of the events list
    }

    // List events by condition, e.g., upcoming events
    public List<Event> getEventsByCondition(Predicate<Event> condition) {
        return events.stream().filter(condition).collect(Collectors.toList());
    }


    // Helper method
    private int findEventIndexByTitle(String title) {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getTitle().equalsIgnoreCase(title)) {
                return i;
            }
        }
        return -1;
    }


    public List<Event> getEventsForPeriod(LocalDate startDate, LocalDate endDate) {
        return events.stream()
                .filter(event -> !event.getDate().isBefore(startDate) && !event.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }


    public List<Event> getPendingTasks() {
        LocalDateTime now = LocalDateTime.now();
        return events.stream()
                .filter(event -> event instanceof Task)
                .map(Task.class::cast)
                .filter(task -> !task.isCompleted() && task.getDeadline().isAfter(now))
                .collect(Collectors.toList());
    }


    public List<Event> getPastDueTasks() {
        LocalDateTime now = LocalDateTime.now();
        return events.stream()
                .filter(event -> event instanceof Task)
                .map(Task.class::cast)
                .filter(task -> !task.isCompleted() && task.getDeadline().isBefore(now))
                .collect(Collectors.toList());
    }
}
