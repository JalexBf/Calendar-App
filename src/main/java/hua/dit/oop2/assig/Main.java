package hua.dit.oop2.assig;

import gr.hua.dit.oop2.calendar.TimeService;
import gr.hua.dit.oop2.calendar.TimeTeller;
import gr.hua.dit.oop2.calendar.TimeListener;
import gr.hua.dit.oop2.calendar.TimeEvent;
import hua.dit.oop2.assig.core.EventManager;
import hua.dit.oop2.assig.core.Event;
import hua.dit.oop2.assig.handlers.CommandProcessor;
import hua.dit.oop2.assig.handlers.ICalHandler;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        ICalHandler fileHandler = new ICalHandler();
        CommandProcessor commandProcessor = new CommandProcessor(fileHandler);

        TimeTeller teller = TimeService.getTeller();
        teller.addTimeListener(new TimeListener() {
            public void timeChanged(TimeEvent e) {
                checkForUpcomingEvents(e.getDateTime());
            }
        });

        commandProcessor.process(args);
        System.exit(0);
    }

    // This part will be used for notifications later
    private static void checkForUpcomingEvents(LocalDateTime currentDateTime) {
        EventManager eventManager = EventManager.getInstance();
        // Check for events happening within the next hour
        for (Event event : eventManager.getAllEvents()) {
            if (event.getDate().isEqual(currentDateTime.toLocalDate()) &&
                    event.getStartTime() != null &&
                    event.getStartTime().isAfter(currentDateTime.toLocalTime()) &&
                    event.getStartTime().isBefore(currentDateTime.toLocalTime().plusHours(1))) {
            }
        }
    }
}
