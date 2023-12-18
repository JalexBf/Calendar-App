package hua.dit.oop2.assig.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import gr.hua.dit.oop2.calendar.TimeService;
import gr.hua.dit.oop2.calendar.TimeTeller;

public class Task extends Event {
    private LocalDateTime deadline;
    private boolean isCompleted;
    private boolean isOverdue;
    private static final TimeTeller teller = TimeService.getTeller();

    // Updated constructor
    public Task(String title, String description, LocalDateTime deadline) {
        super(title, description, deadline.toLocalDate(), deadline.toLocalTime());
        this.deadline = deadline;
        this.isCompleted = false; // New tasks are incomplete by default
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

    // Method to set the task as overdue
    public void setOverdue(boolean overdue) {
        isOverdue = overdue;
    }

    // Method to check if the task is overdue
    public boolean isOverdue() {
        return isOverdue;
    }

}

