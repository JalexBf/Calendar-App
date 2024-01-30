package hua.dit.oop2.assig.GUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import hua.dit.oop2.assig.core.Event;
import hua.dit.oop2.assig.core.EventManager;
import hua.dit.oop2.assig.handlers.ReminderHandler;
import java.util.List;


public class MainApplicationWindow extends JFrame {

    private EventManager eventManager;
    private JTable eventTable;
    private JFileChooser fileChooser;
    private JLabel statusBar;
    private EventListView eventListView;

    public MainApplicationWindow() {
        eventManager = EventManager.getInstance();

        // Set up the main window
        setTitle("Calendar Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set up the layout
        setLayout(new BorderLayout());

        // Create and add components
        createMenuBar();
        createEventDisplayArea();
        createFileChooser();
        createStatusBar();

        // Initialize ReminderService to check for upcoming events and display reminders
        new ReminderHandler();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu eventMenu = new JMenu("Events");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");
        JMenuItem createEventItem = new JMenuItem("Create Event");
        JMenuItem editEventItem = new JMenuItem("Edit Event");

        openItem.addActionListener(e -> openFile());
        saveItem.addActionListener(e -> saveFile());
        exitItem.addActionListener(e -> System.exit(0));
        createEventItem.addActionListener(e -> createEvent());
        editEventItem.addActionListener(e -> editEvent());

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        eventMenu.add(createEventItem);
        eventMenu.add(editEventItem);

        menuBar.add(fileMenu);
        menuBar.add(eventMenu);
        setJMenuBar(menuBar);
    }

    private void createEvent() {
        EventEditorDialog eventEditorDialog = new EventEditorDialog(this, null); // null for creating a new event
        eventEditorDialog.setVisible(true);
        refreshEventView(); // Refresh event view after creating a new event
    }


    private void editEvent() {
        // Check if there are events to edit
        if (eventManager.getAllEvents().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No events available to edit.", "No Events", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow >= 0) {
            Event eventToEdit = eventManager.getAllEvents().get(selectedRow);
            EventEditorDialog eventEditorDialog = new EventEditorDialog(this, eventToEdit);
            eventEditorDialog.setVisible(true);
            refreshEventView(); // Refresh the event view after editing
        } else {
            JOptionPane.showMessageDialog(this, "Please select an event to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }


    private void refreshEventView() {
        eventListView.refreshEventsView();
    }


    public JTable getEventTable() {
        return this.eventTable;
    }


    // In MainApplicationWindow class
    private void createEventDisplayArea() {
        eventListView = new EventListView();
        eventTable = eventListView.getEventTable();
        TaskPanel taskPanel = new TaskPanel(); // Create a TaskPanel instance

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Events", eventListView);
        tabbedPane.addTab("Tasks", taskPanel); // Add TaskPanel as a new tab

        AppointmentPanel appointmentPanel = new AppointmentPanel();
        tabbedPane.addTab("Appointments", appointmentPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }


    private void createFileChooser() {
        fileChooser = new CalendarFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    }


    private void createStatusBar() {
        statusBar = new StatusBar();
        add(statusBar, BorderLayout.SOUTH); // Add to the bottom of the layout
    }


    private void openFile() {
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // Directly use the File object
                List<Event> events = ((CalendarFileChooser) fileChooser).openICalFile(selectedFile);
                eventManager.setEvents(events);
                eventListView.refreshEventsView();
                statusBar.setText("Opened: " + selectedFile.getPath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error reading file: " + e.getMessage(),
                        "File Read Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void saveFile() {
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // Directly use the File object
                List<Event> events = eventManager.getAllEvents();
                ((CalendarFileChooser) fileChooser).saveICalFile(events, selectedFile);
                statusBar.setText("Saved to: " + selectedFile.getPath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error writing file: " + e.getMessage(),
                        "File Write Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainApplicationWindow mainWindow = new MainApplicationWindow();
            mainWindow.setVisible(true);
        });
    }
}
