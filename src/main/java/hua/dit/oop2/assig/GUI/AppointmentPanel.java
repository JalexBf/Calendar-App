package hua.dit.oop2.assig.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

import hua.dit.oop2.assig.core.Appointment;
import hua.dit.oop2.assig.core.EventManager;

public class AppointmentPanel extends JPanel {
    private JTable appointmentTable;
    private AppointmentTableModel appointmentTableModel;

    public AppointmentPanel() {
        setLayout(new BorderLayout()); // Use BorderLayout for panel layout
        EventManager eventManager = EventManager.getInstance();

        // Get only appointments from all events
        List<Appointment> appointments = eventManager.getAllEvents().stream()
                .filter(e -> e instanceof Appointment)
                .map(e -> (Appointment) e)
                .collect(Collectors.toList());

        appointmentTableModel = new AppointmentTableModel(appointments);
        appointmentTable = new JTable(appointmentTableModel);

        // Add the table to a scroll pane for scrolling capability
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        appointmentTable.setFillsViewportHeight(true);

        add(scrollPane, BorderLayout.CENTER); // Add scroll pane to center
    }

    // Method to refresh the appointment view
    public void refreshView() {
        EventManager eventManager = EventManager.getInstance();
        List<Appointment> appointments = eventManager.getAllEvents().stream()
                .filter(e -> e instanceof Appointment)
                .map(e -> (Appointment) e)
                .collect(Collectors.toList());

        appointmentTableModel.refreshAppointments(appointments);
    }
}
