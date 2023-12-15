package hua.dit.oop2.assig;

public class Main {
    public static void main(String[] args) {

        EventManager eventManager = new EventManager();
        ICalHandler fileHandler = new ICalHandler();
        CommandProcessor commandProcessor = new CommandProcessor(eventManager, fileHandler);
        
        commandProcessor.process(args);
    }
}
