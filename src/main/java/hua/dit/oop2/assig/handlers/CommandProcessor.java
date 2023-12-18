package hua.dit.oop2.assig.handlers;

import gr.hua.dit.oop2.calendar.TimeService;
import gr.hua.dit.oop2.calendar.TimeTeller;
import hua.dit.oop2.assig.core.Event;
import hua.dit.oop2.assig.core.EventManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    public void process(String[] args) {
        if (args.length == 0) {
            System.out.println("No arguments provided. Exiting...");
            return;
        }

        String command = args[0].toLowerCase();
        LocalDate currentDate = teller.now().toLocalDate();

        if (args.length == 2) {
            String icalFilePath = args[1];
            fileHandler.readFromICalFile(icalFilePath).forEach(EventManager.getInstance()::addEvent);
            processDisplayCommand(command, currentDate);
        } else if (args.length == 1) {
            String icalFilePath = args[0];
            updateCalendar(icalFilePath);
        } else {
            System.out.println("Invalid number of arguments. Exiting...");
        }
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

    private void updateCalendar(String icalFilePath) {
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
