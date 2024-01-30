package hua.dit.oop2.assig.handlers;

import gr.hua.dit.oop2.calendar.TimeService;
import gr.hua.dit.oop2.calendar.TimeTeller;
import hua.dit.oop2.assig.core.Event;
import hua.dit.oop2.assig.core.EventManager;
import hua.dit.oop2.assig.core.Task;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class CommandProcessor {

    private ICalHandler fileHandler;
    private EventHandler eventHandler;
    private TimeTeller teller;


    public CommandProcessor(ICalHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.eventHandler = new EventHandler();
        this.teller = TimeService.getTeller();
    }


    public void process(String[] args) throws IOException {
        if (args.length < 2) {  // Require at least a command and a file name
            showHelp();
            return;
        }

        String command = args[0].toLowerCase();
        String icalFilePath = args[1];  // Directly use the second argument as the icalFilePath

        // Ensure the file exists or is accessible before proceeding
        if (!new File(icalFilePath).exists()) {
            System.out.println("The specified iCal file does not exist: " + icalFilePath);
            return;
        }

        // Load events from the specified iCal file
        EventManager.getInstance().loadEventsFromFile(icalFilePath);


        switch (command) {
            case "add":
                handleAddEvents(icalFilePath);
                break;
            case "update":
                handleUpdateEvents(icalFilePath);
                break;
            case "view":
                handleViewEventsWithPrompt(icalFilePath);
                break;
            case "update-task":
                handleUpdateTaskStatus(icalFilePath);
                break;
            default:
                System.out.println("Unknown command.");
                showHelp();
                break;
        }
    }



    private void handleAddEvents(String icalFilePath) throws IOException {
        // Logic to handle adding new events
        eventHandler.addEventsFromUserInput();
        saveEventsToFile(icalFilePath);
    }


    private void handleUpdateEvents(String icalFilePath) throws IOException {
        eventHandler.updateEventFromUserInput();
        saveEventsToFile(icalFilePath);
    }


    private void saveEventsToFile(String icalFilePath) throws IOException {
        List<Event> updatedEvents = EventManager.getInstance().getAllEvents();
        fileHandler.writeToICalFile(icalFilePath, updatedEvents);
        System.out.println("Calendar updated successfully.");
        System.out.println("Total events now in the calendar: " + updatedEvents.size());
    }


    private void showHelp() {
        System.out.println("Usage:");
        System.out.println("  java -jar [jarfile] add [icalFilePath]       - Add new events");
        System.out.println("  java -jar [jarfile] update [icalFilePath]    - Update existing events");
        System.out.println("  java -jar [jarfile] view [icalFilePath]      - View events from the specified iCal file. You will be prompted for the period (e.g., day, week, month).");
    }


    private void handleUpdateTaskStatus(String icalFilePath) throws IOException {
        Scanner scanner = new Scanner(System.in);
        EventManager eventManager = EventManager.getInstance();

        // List tasks with index
        eventManager.listAllTasks();

        try {
            // Ask user to select task by index
            System.out.println("Enter the number of the task to update:");
            int taskIndex = scanner.nextInt();
            scanner.nextLine(); // Clear the buffer

            Task selectedTask = eventManager.getTaskByIndex(taskIndex);

            if (selectedTask != null) {
                // Ask for the new status
                System.out.println("Enter new status (true for completed, false for not completed):");
                boolean newStatus = scanner.nextBoolean();
                scanner.nextLine(); // Clear the buffer

                // Update task status
                String taskId = selectedTask.getUuid();
                String result = eventManager.updateTaskStatus(taskId, newStatus);
                System.out.println(result);
            } else {
                System.out.println("Invalid task number.");
            }
        } catch (InputMismatchException ime) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear the buffer
        }

        saveEventsToFile(icalFilePath);
    }


    private void handleViewEventsWithPrompt(String icalFilePath) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select the period for which you want to view events:");
        System.out.println("1. Day");
        System.out.println("2. Week");
        System.out.println("3. Month");
        System.out.println("4. Past Day");
        System.out.println("5. Past Week");
        System.out.println("6. Past Month");
        System.out.println("7. Next Day");
        System.out.println("8. Next Week");
        System.out.println("9. Todo (Incomplete Tasks)");
        System.out.println("10. Due (Overdue Tasks)");
        String choice = scanner.nextLine().trim();

        LocalDate currentDate = LocalDate.now();
        String period = getPeriodFromChoice(choice);

        if (!period.isEmpty()) {
            eventHandler.viewEvents(period, currentDate);  // Call the method to display events for the chosen period
        } else {
            System.out.println("Invalid choice. Please restart the command and select a valid option.");
        }
    }


    private String getPeriodFromChoice(String choice) {
        switch (choice) {
            case "1": return "day";
            case "2": return "week";
            case "3": return "month";
            case "4": return "pastday";
            case "5": return "pastweek";
            case "6": return "pastmonth";
            case "7": return "nextday";
            case "8": return "nextweek";
            case "9": return "todo";
            case "10": return "due";
            default: return "";
        }
    }



    private void handleUserCommands(String icalFilePath) throws IOException {
        Scanner scanner = new Scanner(System.in);
        boolean keepRunning = true;

        while (keepRunning) {
            System.out.println("Choose an option: \n1. Add new event \n2. Update an event \n3. Exit");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    eventHandler.addEventsFromUserInput();
                    break;
                case "2":
                    eventHandler.updateEventFromUserInput();
                    break;
                case "3":
                    keepRunning = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }

        List<Event> updatedEvents = EventManager.getInstance().getAllEvents();
        fileHandler.writeToICalFile(icalFilePath, updatedEvents);
        System.out.println("Calendar updated successfully.");
    }


    private void processDisplayCommand(String command, LocalDate currentDate) {
        switch (command) {
            case "day":
                eventHandler.viewEvents("day", currentDate);
                break;
            case "week":
                eventHandler.viewEvents("week", currentDate);
                break;
            case "month":
                eventHandler.viewEvents("month", currentDate);
                break;
            case "pastday":
                eventHandler.viewEvents("day", currentDate.minusDays(1));
                break;
            case "pastweek":
                eventHandler.viewEvents("week", currentDate.minusWeeks(1));
                break;
            case "pastmonth":
                eventHandler.viewEvents("month", currentDate.minusMonths(1));
                break;
            case "todo":
                eventHandler.viewPendingTasks();
                break;
            case "due":
                eventHandler.viewPastDueTasks();
                break;
            default:
                System.out.println("Unknown display command.");
                break;
        }
    }

    private void updateCalendar(String icalFilePath) throws IOException {
        eventHandler.addEventsFromUserInput();
        List<Event> updatedEvents = EventManager.getInstance().getAllEvents();
        fileHandler.writeToICalFile(icalFilePath, updatedEvents);
        System.out.println("Calendar updated successfully with new events.");
    }


    private LocalDate promptForDate(Scanner scanner, String promptMessage) {
        LocalDate date = null;
        while (date == null) {
            System.out.println(promptMessage);
            String input = scanner.nextLine();
            try {
                date = LocalDate.parse(input, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter the date in DD-MM-YYYY format.");
            }
        }
        return date;
    }

    private LocalTime promptForTime(Scanner scanner, String promptMessage) {
        LocalTime time = null;
        while (time == null) {
            System.out.println(promptMessage);
            String input = scanner.nextLine();
            try {
                time = LocalTime.parse(input, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format. Please enter the time in HH:mm format.");
            }
        }
        return time;
    }




}
