package hua.dit.oop2.assig;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task extends Event{
    private LocalDateTime deadline;
    private boolean isCompleted;

    public Task(String title, String description, LocalDate date, LocalDateTime deadline) {
        super(title, description, date);
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
}
