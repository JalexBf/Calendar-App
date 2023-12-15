package hua.dit.oop2.assig;

import java.time.LocalDate;
import java.time.LocalTime;

public abstract class Event {
    private String title;
    private String description;
    private LocalDate date;
    private boolean isAllDay;


    // Constructor for timed event
    public Event(String title, String description, LocalDate date, LocalTime startTime) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.isAllDay = false;
    }

    // Constructor for all-day event
    public Event(String title, String description, LocalDate date) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.isAllDay = true;
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

    public boolean isAllDay() {

        return isAllDay;
    }
}
