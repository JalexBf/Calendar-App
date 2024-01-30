package hua.dit.oop2.assig.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import hua.dit.oop2.assig.core.Event;
import hua.dit.oop2.assig.core.Appointment;
import hua.dit.oop2.assig.core.EventManager;
import hua.dit.oop2.assig.core.Task;


public class EventEditorDialog extends JDialog {

    private JTextField titleField;
    private JTextArea descriptionField;
    private JTextField dateField;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JCheckBox isAllDayCheckbox;
    private JCheckBox isCompletedCheckbox;
    private JButton saveButton;
    private JButton cancelButton;

    private Event event; // The event being edited, null if it's a new event
    private JComboBox<String> eventTypeComboBox;
    private JPanel dynamicFieldsPanel;


    public EventEditorDialog(Frame owner, Event event) {
        super(owner, "Edit Event", true);
        this.event = event;

        setupForm();
        populateFields();

        saveButton.addActionListener(e -> saveEvent());
        cancelButton.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(owner);
    }


    private void setupForm() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 2));

        titleField = new JTextField();
        descriptionField = new JTextArea(5, 20);
        dateField = new JTextField();
        startTimeField = new JTextField();
        endTimeField = new JTextField();
        isAllDayCheckbox = new JCheckBox("All Day Event");
        isAllDayCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Enable or disable time fields based on the checkbox state
                toggleTimeFieldsEnabled(!isAllDayCheckbox.isSelected());
            }
        });
        isCompletedCheckbox = new JCheckBox("Task Completed");

        // Event type selection combo box
        eventTypeComboBox = new JComboBox<>(new String[]{"Event", "Appointment", "Task"});
        eventTypeComboBox.addActionListener(e -> updateFormFields());

        formPanel.add(new JLabel("Event Type:"));
        formPanel.add(eventTypeComboBox);

        dynamicFieldsPanel = new JPanel(new CardLayout());
        setupEventFields();    // Call methods to setup fields for each type
        setupAppointmentFields();
        setupTaskFields();

        // Add dynamic fields panel to formPanel or another appropriate place in the layout
        formPanel.add(dynamicFieldsPanel);
        formPanel.add(titleField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descriptionField));
        formPanel.add(new JLabel("Date (dd-mm-yyyy):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Start Time (HH:mm):"));
        formPanel.add(startTimeField);
        formPanel.add(new JLabel("End Time (HH:mm):"));
        formPanel.add(endTimeField);
        formPanel.add(isAllDayCheckbox);
        formPanel.add(isCompletedCheckbox);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }


    private void updateFormFields() {
        // Use CardLayout to switch between different sets of input fields
        CardLayout cardLayout = (CardLayout) dynamicFieldsPanel.getLayout();
        String selectedType = (String) eventTypeComboBox.getSelectedItem();

        cardLayout.show(dynamicFieldsPanel, selectedType);
    }


    private void populateFields() {
        if (event != null) {
            // Set combo box to the type of the event
            if (event instanceof Appointment) {
                eventTypeComboBox.setSelectedItem("Appointment");
                // Populate appointment fields
            } else if (event instanceof Task) {
                eventTypeComboBox.setSelectedItem("Task");
                // Populate task fields
            } else {
                eventTypeComboBox.setSelectedItem("Event");

            }
            updateFormFields(); // Update form to show the correct fields
        }
    }


    private void toggleTimeFieldsEnabled(boolean enabled) {
        startTimeField.setEnabled(enabled);
        endTimeField.setEnabled(enabled);
    }


    private void saveEvent() {
        try {
            String title = titleField.getText();
            String description = descriptionField.getText();
            LocalDate date = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            EventManager eventManager = EventManager.getInstance(); // Get instance of EventManager

            String selectedType = (String) eventTypeComboBox.getSelectedItem();

            switch (selectedType) {
                case "Event":
                    if (event == null) {
                        event = new Event(title, description, date); // Create a new general event
                    } else {
                        // Update existing event
                        event.setTitle(title);
                        event.setDescription(description);
                        event.setDate(date);
                    }
                    break;
                case "Appointment":
                    LocalTime startTime = LocalTime.parse(startTimeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
                    LocalTime endTime = LocalTime.parse(endTimeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));

                    if (event == null) {
                        event = new Appointment(title, description, date, startTime, endTime); // Create a new appointment
                    } else {
                        // Update existing appointment
                        Appointment appointment = (Appointment) event;
                        appointment.setTitle(title);
                        appointment.setDescription(description);
                        appointment.setDate(date);
                        appointment.setStartTime(startTime);
                        appointment.setEndTime(endTime);
                    }
                    break;
                case "Task":
                    LocalTime deadlineTime = LocalTime.parse(startTimeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
                    boolean isCompleted = isCompletedCheckbox.isSelected();

                    if (event == null) {
                        event = new Task(title, description, LocalDateTime.of(date, deadlineTime)); // Create a new task
                        ((Task) event).setCompleted(isCompleted);
                    } else {
                        // Update existing task
                        Task task = (Task) event;
                        task.setTitle(title);
                        task.setDescription(description);
                        task.setDeadline(LocalDateTime.of(date, deadlineTime));
                        task.setCompleted(isCompleted);
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected event type: " + selectedType);
            }

            // Add or update the event in the event manager
            if (event.getUuid() == null) {
                eventManager.addEvent(event);
            } else {
                eventManager.updateEvent(event.getUuid(), event);
            }

            dispose(); // Close the dialog after saving
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date or time format.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving event: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void setupEventFields() {
        JPanel eventFieldsPanel = new JPanel(new GridLayout(0, 2));
        eventFieldsPanel.add(new JLabel("Title:"));
        eventFieldsPanel.add(titleField);
        eventFieldsPanel.add(new JLabel("Description:"));
        eventFieldsPanel.add(new JScrollPane(descriptionField));
        eventFieldsPanel.add(new JLabel("Date (dd-mm-yyyy):"));
        eventFieldsPanel.add(dateField);

        dynamicFieldsPanel.add(eventFieldsPanel, "Event");
    }


    private void setupAppointmentFields() {
        JPanel appointmentFieldsPanel = new JPanel(new GridLayout(0, 2));
        // Appointments have start time and end time
        appointmentFieldsPanel.add(new JLabel("Title:"));
        appointmentFieldsPanel.add(titleField);
        appointmentFieldsPanel.add(new JLabel("Description:"));
        appointmentFieldsPanel.add(new JScrollPane(descriptionField));
        appointmentFieldsPanel.add(new JLabel("Date (dd-mm-yyyy):"));
        appointmentFieldsPanel.add(dateField);
        appointmentFieldsPanel.add(new JLabel("Start Time (HH:mm):"));
        appointmentFieldsPanel.add(startTimeField);
        appointmentFieldsPanel.add(new JLabel("End Time (HH:mm):"));
        appointmentFieldsPanel.add(endTimeField);

        dynamicFieldsPanel.add(appointmentFieldsPanel, "Appointment");
    }


    private void setupTaskFields() {
        JPanel taskFieldsPanel = new JPanel(new GridLayout(0, 2));
        // Tasks have a deadline and a completed status
        taskFieldsPanel.add(new JLabel("Title:"));
        taskFieldsPanel.add(titleField);
        taskFieldsPanel.add(new JLabel("Description:"));
        taskFieldsPanel.add(new JScrollPane(descriptionField));
        taskFieldsPanel.add(new JLabel("Deadline Date (dd-mm-yyyy):"));
        taskFieldsPanel.add(dateField);
        taskFieldsPanel.add(new JLabel("Deadline Time (HH:mm):"));
        taskFieldsPanel.add(startTimeField); // Reusing startTimeField for deadline time
        taskFieldsPanel.add(isCompletedCheckbox);

        dynamicFieldsPanel.add(taskFieldsPanel, "Task");
    }




}
