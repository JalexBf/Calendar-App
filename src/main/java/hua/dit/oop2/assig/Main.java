package hua.dit.oop2.assig;

import hua.dit.oop2.assig.handlers.CommandProcessor;
import hua.dit.oop2.assig.handlers.ICalHandler;
import hua.dit.oop2.assig.handlers.ReminderHandler;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ICalHandler fileHandler = new ICalHandler();
        CommandProcessor commandProcessor = new CommandProcessor(fileHandler);

        // Process commands, potentially loading events
        commandProcessor.process(args);

        // Initialize ReminderService after events are loaded
        ReminderHandler reminderHandler = new ReminderHandler();

        System.exit(0);
    }
}



