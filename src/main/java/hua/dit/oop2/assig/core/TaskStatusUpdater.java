package hua.dit.oop2.assig.core;

import gr.hua.dit.oop2.calendar.TimeListener;
import gr.hua.dit.oop2.calendar.TimeEvent;
import hua.dit.oop2.assig.core.EventManager;
import hua.dit.oop2.assig.core.Task;

import java.time.LocalDateTime;
import java.util.List;

public class TaskStatusUpdater implements TimeListener {
    private EventManager eventManager;

    public TaskStatusUpdater(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public void timeChanged(TimeEvent e) {
        LocalDateTime currentTime = e.getDateTime();
        // Update logic for task status
        updateTaskStatuses(currentTime);
    }

    private void updateTaskStatuses(LocalDateTime currentTime) {
        // Retrieve all tasks
        List<Task> tasks = eventManager.getAllTasks();

        // Iterate through tasks and update their status based on the current time
        for (Task task : tasks) {
            if (!task.isCompleted() && task.getDeadline().isBefore(currentTime)) {
                task.setCompleted(true); // Mark task as completed if the deadline has passed
            }
        }
    }
}
