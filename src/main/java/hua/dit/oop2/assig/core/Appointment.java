package hua.dit.oop2.assig.core;

import java.time.LocalDate;
import java.time.LocalTime;
import gr.hua.dit.oop2.calendar.TimeService;
import gr.hua.dit.oop2.calendar.TimeTeller;

public class Appointment extends Event {
    private LocalTime startTime;
    private LocalTime endTime;
    private static final TimeTeller teller = TimeService.getTeller();

    public Appointment(String title, String description, LocalDate date, LocalTime startTime, LocalTime endTime) {
        super(title, description, date);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isHappeningNow() {
        LocalDate currentDate = teller.now().toLocalDate();
        LocalTime currentTime = teller.now().toLocalTime();

        return currentDate.equals(getDate()) &&
                currentTime.isAfter(getStartTime()) &&
                currentTime.isBefore(getEndTime());
    }
}
