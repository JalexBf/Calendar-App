package hua.dit.oop2.assig.core;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import gr.hua.dit.oop2.calendar.TimeService;
import gr.hua.dit.oop2.calendar.TimeTeller;

public class Event {
    private String uuid;    // Unique identifier
    private String title;
    private String description;
    private LocalDate date;
    private LocalTime startTime;
    private boolean isAllDay;

    // Static TimeTeller instance for time-related functionalities
    private static final TimeTeller teller = TimeService.getTeller();

    // Constructor for timed event
    public Event(String title, String description, LocalDate date, LocalTime startTime) {
        this.uuid = UUID.randomUUID().toString();
        this.title = title != null ? title : "No title";
        this.description = description;
        this.date = date;
        this.startTime = startTime;
        this.isAllDay = false;
    }

    // Separate constructor for all-day events
    public Event(String title, String description, LocalDate date) {
        this.uuid = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.date = date;
        this.isAllDay = true;
        this.startTime = null; // No specific start time for all-day events
    }


    public boolean isStartingSoon() {
        LocalDate currentDate = teller.now().toLocalDate();
        LocalTime currentTime = teller.now().toLocalTime();

        // Check if the event is today and starting within the next hour
        return this.date.equals(currentDate) && this.startTime != null &&
                this.startTime.isAfter(currentTime) &&
                this.startTime.minusHours(1).isBefore(currentTime);
    }


    // Getters and Setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public boolean isAllDay() {
        return isAllDay;
    }

}
