package hua.dit.oop2.assig.GUI;

import javax.swing.table.AbstractTableModel;
import hua.dit.oop2.assig.core.Appointment;
import java.util.List;

public class AppointmentTableModel extends AbstractTableModel {

    private final List<Appointment> appointments;
    private final String[] columnNames = {"Title", "Description", "Date", "Start Time", "End Time"};

    public AppointmentTableModel(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    @Override
    public int getRowCount() {
        return appointments.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Appointment appointment = appointments.get(rowIndex);
        switch (columnIndex) {
            case 0: return appointment.getTitle();
            case 1: return appointment.getDescription();
            case 2: return appointment.getDate();
            case 3: return appointment.getStartTime();
            case 4: return appointment.getEndTime();
            default: return "N/A";
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    // Method to refresh appointments in the table model
    public void refreshAppointments(List<Appointment> newAppointments) {
        this.appointments.clear();
        this.appointments.addAll(newAppointments);
        fireTableDataChanged();
    }
}
