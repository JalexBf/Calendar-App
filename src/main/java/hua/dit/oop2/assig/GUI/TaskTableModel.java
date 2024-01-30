package hua.dit.oop2.assig.GUI;

import javax.swing.table.AbstractTableModel;
import hua.dit.oop2.assig.core.Task;
import java.util.List;

public class TaskTableModel extends AbstractTableModel {

    private final List<Task> tasks;
    private final String[] columnNames = {"Title", "Description", "Deadline", "Status"};

    public TaskTableModel(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public int getRowCount() {
        return tasks.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Task task = tasks.get(rowIndex);
        switch (columnIndex) {
            case 0: return task.getTitle();
            case 1: return task.getDescription();
            case 2: return task.getDeadline();
            case 3: return task.isCompleted() ? "Completed" : "Not Completed";
            default: return "N/A";
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    // Method to refresh the tasks in the table model
    public void refreshTasks(List<Task> newTasks) {
        this.tasks.clear();
        this.tasks.addAll(newTasks);
        fireTableDataChanged();
    }
}
