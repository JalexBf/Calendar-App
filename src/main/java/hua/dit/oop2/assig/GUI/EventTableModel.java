package hua.dit.oop2.assig.GUI;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import hua.dit.oop2.assig.core.Event;
import hua.dit.oop2.assig.core.Appointment;
import hua.dit.oop2.assig.core.Task;

import javax.swing.table.AbstractTableModel;
import hua.dit.oop2.assig.core.Event;
import hua.dit.oop2.assig.core.Appointment;
import hua.dit.oop2.assig.core.Task;
import hua.dit.oop2.assig.core.EventManager;
import java.util.List;

public class EventTableModel extends AbstractTableModel {

    private final List<Event> events;
    private final String[] columnNames = {"Title", "Description", "Date", "Start Time", "End Time/Deadline", "Status"};
    private final EventManager eventManager = EventManager.getInstance();

    public EventTableModel(List<Event> events) {
        this.events = events;
    }

    @Override
    public int getRowCount() {
        return events.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Event event = events.get(rowIndex);
        switch (columnIndex) {
            case 0: return event.getTitle();
            case 1: return event.getDescription();
            case 2: return event.getDate();
            case 3:
                if (event instanceof Appointment) {
                    return ((Appointment) event).getStartTime();
                }
                return "";
            case 4:
                if (event instanceof Appointment) {
                    return ((Appointment) event).getEndTime();
                } else if (event instanceof Task) {
                    return ((Task) event).getDeadline();
                }
                return "";
            case 5:
                if (event instanceof Task) {
                    return ((Task) event).isCompleted() ? "Completed" : "Not Completed";
                }
                return "";
            default: return "N/A";
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public void addEvent(Event event) {
        eventManager.addEvent(event);
        this.events.add(event);
        fireTableRowsInserted(events.size() - 1, events.size() - 1);
    }

    public void removeEvent(int rowIndex) {
        Event eventToRemove = events.get(rowIndex);
        events.remove(eventToRemove);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void updateEvent(int rowIndex, Event updatedEvent) {
        Event eventToUpdate = events.get(rowIndex);
        eventManager.updateEvent(eventToUpdate.getUuid(), updatedEvent);
        events.set(rowIndex, updatedEvent);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    // Refresh table model with the latest events
    public void refreshEvents(List<Event> newEvents) {
        this.events.clear();
        this.events.addAll(newEvents);
        fireTableDataChanged(); // Notify table that data has changed
    }
}
