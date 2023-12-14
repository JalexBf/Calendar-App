package hua.dit.oop2.assig;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment extends Event{
    private LocalTime startTime;
    private Duration duration;

    public Appointment(String title, String description, LocalDate date, LocalTime startTime, Duration duration) {
        super(title, description, date);
        this.startTime = startTime;
        this.duration = duration;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
