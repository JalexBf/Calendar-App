package hua.dit.oop2.assig.GUI;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

import hua.dit.oop2.assig.core.Appointment;
import hua.dit.oop2.assig.core.EventManager;
import hua.dit.oop2.assig.core.Event;
import hua.dit.oop2.assig.core.Task;

import java.util.function.Predicate;
import java.util.List;
import java.util.stream.Collectors;

public class EventListView extends JPanel {
    private JTable eventTable;
    private EventTableModel eventTableModel;
    private EventManager eventManager;


    public JTable getEventTable() {
        return this.eventTable;
    }


    public void updateEventList(List<Event> events) {
        eventTableModel.refreshEvents(events);
    }


    // Method to refresh the table with new data
    public void refreshEventsView() {
        // Filter the list to exclude tasks and appointments
        List<Event> filteredEvents = eventManager.getAllEvents().stream()
                .filter(e -> !(e instanceof Task) && !(e instanceof Appointment))
                .collect(Collectors.toList());

        // Update the event list in the table model
        eventTableModel.refreshEvents(filteredEvents);
    }


    public EventListView() {
        setLayout(new BorderLayout());
        eventManager = EventManager.getInstance();
        eventTableModel = new EventTableModel(eventManager.getAllEvents());
        eventTable = new JTable(eventTableModel);

        JScrollPane scrollPane = new JScrollPane(eventTable);
        add(scrollPane, BorderLayout.CENTER);
    }




}

