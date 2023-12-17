package hua.dit.oop2.assig.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import gr.hua.dit.oop2.calendar.TimeService;
import gr.hua.dit.oop2.calendar.TimeTeller;

public class Task extends Event {
    private LocalDateTime deadline;
    private boolean isCompleted;
    private static final TimeTeller teller = TimeService.getTeller();

    public Task(String title, String description, LocalDate date, LocalDateTime deadline) {
        super(title, description, date, deadline.toLocalTime()); // Assuming the time part of deadline is the start time
        this.deadline = deadline;
        this.isCompleted = false;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isPastDue() {
        return teller.now().isAfter(deadline) && !isCompleted;
    }
}
