package hua.dit.oop2.assig;

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

    // Delete an event from the collection
    public void deleteEvent(String title) {
        events.removeIf(event -> event.getTitle().equals(title));
    }

    // List all events
    public List<Event> getAllEvents() {
        return new ArrayList<>(events); // Return a copy of the events list
    }

    // List events by condition, e.g., upcoming events
    public List<Event> getEventsByCondition(Predicate<Event> condition) {
        return events.stream().filter(condition).collect(Collectors.toList());
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
}