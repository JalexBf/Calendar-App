package hua.dit.oop2.assig.core;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import gr.hua.dit.oop2.calendar.TimeService;
import gr.hua.dit.oop2.calendar.TimeTeller;
import hua.dit.oop2.assig.handlers.ICalHandler;


public class EventManager {
    private static EventManager instance = null;
    private List<Event> events;

    private List<Task> tasks;       // List for tasks
    private static final TimeTeller teller = TimeService.getTeller();

    public EventManager() {
        this.events = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }


    public List<Event> getEvents() {
        return new ArrayList<>(events); // Returns a copy of the events list
    }

    public void setEvents(List<Event> newEvents) {
        this.events.clear();
        this.events.addAll(newEvents);

        // Update tasks list if any of the new events are tasks
        this.tasks.clear();
        for (Event event : newEvents) {
            if (event instanceof Task) {
                this.tasks.add((Task) event);
            }
        }
    }


    public void loadEventsFromFile(String icalFilePath) throws IOException {
        ICalHandler icalHandler = new ICalHandler(); // Create an ICalHandler instance to read the file
        List<Event> loadedEvents = icalHandler.readFromICalFile(icalFilePath); // Read events from the file

        // Clear existing events and tasks lists
        this.events.clear();
        this.tasks.clear();

        // Process each loaded event
        for (Event event : loadedEvents) {
            // Add to the main events list
            this.events.add(event);

            // If event is a task, update its overdue status and add it to the tasks list
            if (event instanceof Task) {
                Task task = (Task) event;

                // Explicitly set the overdue status based on the current time and deadline
                task.setOverdue(task.checkIfOverdue());

                // Add to tasks list
                this.tasks.add(task);
            }
        }
    }



    // Public static method to get the instance
    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }


    // Add event to the collection
    public void addEvent(Event event) {
        if (events.stream().noneMatch(e -> e.getUuid().equals(event.getUuid()))) {
            events.add(event);

            // If the event is a task, add it to the tasks list
            if (event instanceof Task) {
                tasks.add((Task) event);
            }
        } else {
            System.out.println("An event with this UUID already exists.");
        }
    }


    // Method to find an event by ttitle
    public List<Event> findEventsByTitle(String title) {
        return events.stream()
                .filter(event -> event.getTitle().equalsIgnoreCase(title))
                .collect(Collectors.toList());
    }


    public Event findEventByUuid(String uuid) {
        return events.stream()
                .filter(event -> event.getUuid().equals(uuid))
                .findFirst()
                .orElse(null);
    }



    // Edit event in the collection
    public void updateEvent(String uuid, Event updatedEvent) {
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            if (event.getUuid().equals(uuid)) {
                events.set(i, updatedEvent);
                if (updatedEvent instanceof Task) {
                    updateTaskInList((Task) updatedEvent);
                }
                return;
            }
        }
        System.out.println("Event with the specified UUID not found.");
    }


    private void updateTaskInList(Task updatedTask) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getUuid().equals(updatedTask.getUuid())) {
                tasks.set(i, updatedTask);
                return;
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


    // Method to get a task by index
    public Task getTaskByIndex(int index) {
        if (index >= 1 && index <= tasks.size()) {
            return tasks.get(index - 1); // Adjust for zero-based index
        }
        return null; // Invalid index
    }



    public void listAllTasks() {
        int index = 1; // Start index from 1 for user-friendliness
        for (Task task : tasks) {
            System.out.println("[" + index++ + "] Title: " + task.getTitle()
                    + ", Status: " + (task.isCompleted() ? "Completed" : "Not Completed"));
        }
    }


    public String updateTaskStatus(String taskId, boolean newStatus) {
        for (Task task : tasks) {
            if (task.getUuid().equals(taskId)) {
                task.updateStatus(newStatus);
                return "Task status updated successfully.";
            }
        }
        return "Task with the specified ID not found.";
    }


    private void updateTasksList() {
        this.tasks = events.stream()
                .filter(event -> event instanceof Task)
                .map(event -> (Task) event)
                .collect(Collectors.toList());
    }



}
