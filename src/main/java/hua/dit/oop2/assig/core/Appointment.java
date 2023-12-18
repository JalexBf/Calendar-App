package hua.dit.oop2.assig.core;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import gr.hua.dit.oop2.calendar.TimeService;
import gr.hua.dit.oop2.calendar.TimeTeller;

public class Appointment extends Event {
    private Duration duration;
    private static final TimeTeller teller = TimeService.getTeller();

    public Appointment(String title, String description, LocalDate date, LocalTime startTime, Duration duration) {
        super(title, description, date, startTime);
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }


    public boolean isHappeningNow() {
        LocalDate currentDate = teller.now().toLocalDate();
        LocalTime currentTime = teller.now().toLocalTime();
        LocalTime endTime = getStartTime().plus(duration);

        return currentDate.equals(getDate()) &&
                currentTime.isAfter(getStartTime()) &&
                currentTime.isBefore(endTime);
    }
}
