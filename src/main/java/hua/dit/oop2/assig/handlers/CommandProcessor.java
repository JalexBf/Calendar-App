package hua.dit.oop2.assig.handlers;

import gr.hua.dit.oop2.calendar.TimeService;
import gr.hua.dit.oop2.calendar.TimeTeller;
import hua.dit.oop2.assig.core.Event;
import hua.dit.oop2.assig.core.EventManager;

import java.time.LocalDate;
import java.util.List;

public class CommandProcessor {

    private EventManager manager;
    private ICalHandler fileHandler;
    private EventHandler eventHandler;
    private TimeTeller teller;

    public CommandProcessor(EventManager manager, ICalHandler fileHandler) {
        this.manager = manager;
        this.fileHandler = fileHandler;
        this.eventHandler = new EventHandler(manager);
        this.teller = TimeService.getTeller(); // Initialize TimeTeller from custom library
    }

    public void process(String[] args) {
        if (args.length == 0) {
            System.out.println("No arguments provided. Exiting...");
            return;
        }

        String command = args[0].toLowerCase();
        LocalDate currentDate = teller.now().toLocalDate(); // Get current date using custom library

        if (args.length == 2) {
            String icalFilePath = args[1];
            fileHandler.readFromICalFile(icalFilePath).forEach(manager::addEvent); // Load events from the file
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
        List<Event> updatedEvents = manager.getAllEvents();
        fileHandler.writeToICalFile(icalFilePath, updatedEvents);
        System.out.println("Calendar updated successfully with new events.");
    }
}