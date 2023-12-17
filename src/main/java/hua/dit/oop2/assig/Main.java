package hua.dit.oop2.assig;

import gr.hua.dit.oop2.calendar.TimeService;
import gr.hua.dit.oop2.calendar.TimeTeller;
import gr.hua.dit.oop2.calendar.TimeListener;
import gr.hua.dit.oop2.calendar.TimeEvent;
import hua.dit.oop2.assig.core.EventManager;
import hua.dit.oop2.assig.handlers.CommandProcessor;
import hua.dit.oop2.assig.handlers.ICalHandler;

public class Main {
    public static void main(String[] args) {
        EventManager eventManager = new EventManager();
        ICalHandler fileHandler = new ICalHandler();
        CommandProcessor commandProcessor = new CommandProcessor(eventManager, fileHandler);

        TimeTeller teller = TimeService.getTeller();
        teller.addTimeListener(new TimeListener() {
            public void timeChanged(TimeEvent e) {
                // Implement logic needed for time changes, but avoid console output
            }
        });

        commandProcessor.process(args);
        System.exit(0);
    }
}

