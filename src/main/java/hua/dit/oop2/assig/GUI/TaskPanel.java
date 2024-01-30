package hua.dit.oop2.assig.GUI;

import javax.swing.*;
import java.awt.*;
import hua.dit.oop2.assig.core.EventManager;
import hua.dit.oop2.assig.core.Task;
import java.util.List;

public class TaskPanel extends JPanel {
    private EventManager eventManager;
    private JTable taskTable;
    private TaskTableModel taskTableModel;

    public TaskPanel() {
        eventManager = EventManager.getInstance();
        setLayout(new BorderLayout());

        // Create a table model to hold tasks
        List<Task> taskList = eventManager.getAllTasks();
        taskTableModel = new TaskTableModel(taskList);
        taskTable = new JTable(taskTableModel);

        // Add a scroll pane for the task table
        JScrollPane scrollPane = new JScrollPane(taskTable);
        add(scrollPane, BorderLayout.CENTER);

        // Add a refresh button
        JButton refreshButton = new JButton("Refresh Tasks");
        refreshButton.addActionListener(e -> updateTaskList());

        add(refreshButton, BorderLayout.SOUTH);
    }

    public void updateTaskList() {
        // Refresh the task list from the EventManager
        List<Task> newTaskList = eventManager.getAllTasks();
        taskTableModel.refreshTasks(newTaskList);
    }
}
