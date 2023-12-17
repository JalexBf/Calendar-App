package hua.dit.oop2.assig.core;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import gr.hua.dit.oop2.calendar.TimeService;
import gr.hua.dit.oop2.calendar.TimeTeller;

public class EventManager {
    private List<Event> events;
    private static final TimeTeller teller = TimeService.getTeller();

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

    // List events by condition
    public List<Event> getEventsByCondition(Predicate<Event> condition) {
        return events.stream().filter(condition).collect(Collectors.toList());
    }

    public List<Event> getEventsForPeriod(LocalDate startDate, LocalDate endDate) {
        return events.stream()
                .filter(event -> !event.getDate().isBefore(startDate) && !event.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public List<Event> getPendingTasks() {
        LocalDate now = teller.now().toLocalDate();
        return events.stream()
                .filter(event -> event instanceof Task)
                .map(Task.class::cast)
                .filter(task -> !task.isCompleted() && task.getDeadline().toLocalDate().isAfter(now))
                .collect(Collectors.toList());
    }

    public List<Event> getPastDueTasks() {
        LocalDate now = teller.now().toLocalDate();
        return events.stream()
                .filter(event -> event instanceof Task)
                .map(Task.class::cast)
                .filter(task -> !task.isCompleted() && task.getDeadline().toLocalDate().isBefore(now))
                .collect(Collectors.toList());
    }


    // Method to get a list of all tasks
    public List<Task> getAllTasks() {
        return events.stream()
                .filter(event -> event instanceof Task)
                .map(event -> (Task) event)
                .collect(Collectors.toList());
    }
}
