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


    public Task(String title, String description, LocalDateTime deadline) {
        super(title, description, deadline != null ? deadline.toLocalDate() : null, deadline != null ? deadline.toLocalTime() : null);
        this.deadline = deadline;
        this.isCompleted = false;
        this.isOverdue = checkIfOverdue(); // Call a method to determine if the task is overdue
    }


    // Method to check if the task is overdue
    public boolean checkIfOverdue() {
        return deadline != null && deadline.isBefore(LocalDateTime.now());
    }


    public LocalDateTime getDeadline() {
        return deadline;
    }


    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
        this.isOverdue = checkIfOverdue();
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



    // Method to update the task's status
    public void updateStatus(boolean newStatus) {
        if (this.isCompleted && newStatus) {
            System.out.println("Task is already completed.");
        } else {
            this.isCompleted = newStatus;
            if (newStatus) {
                System.out.println("Task marked as completed.");
            } else {
                System.out.println("Task marked as not completed.");
            }
        }
    }
}

