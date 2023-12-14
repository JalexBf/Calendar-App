package hua.dit.oop2.assig;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class CommandProcessor {

    private EventManager manager;
    private ICalHandler fileHandler;

    public CommandProcessor() {
        this.manager = new EventManager();
        this.fileHandler = new ICalHandler();
    }

    public void process(String[] args) {
        if (args.length == 0) {
            System.out.println("No arguments provided. Exiting...");
            return;
        }

        String command = args[0];

        if (args.length == 2) {
            String icalFilePath = args[1];
            processDisplayCommand(command, icalFilePath);
        } else if (args.length == 1) {
            String icalFilePath = args[0];
            fileHandler.updateCalendar(icalFilePath, manager);
        } else {
            System.out.println("Invalid number of arguments. Exiting...");
        }
    }

    private void processDisplayCommand(String command, String icalFilePath) {
        List<Event> events = fileHandler.readFromICalFile(icalFilePath); // Assuming ICalHandler has this method
        LocalDate currentDate = LocalDate.now();

        switch (command.toLowerCase()) {
            case "day":
                displayEvents(manager.getEventsForDay(currentDate));
                break;
            case "week":
                displayEventsForPeriod(currentDate, ChronoUnit.WEEKS);
                break;
            case "month":
                displayEventsForPeriod(currentDate, ChronoUnit.MONTHS);
                break;
            case "pastday":
                displayEvents(manager.getEventsForDay(currentDate.minusDays(1)));
                break;
            case "pastweek":
                displayEventsForPeriod(currentDate.minusWeeks(1), ChronoUnit.WEEKS);
                break;
            case "pastmonth":
                displayEventsForPeriod(currentDate.minusMonths(1), ChronoUnit.MONTHS);
                break;
            case "todo":
                displayEvents(manager.getPendingTasks());
                break;
            case "due":
                displayEvents(manager.getPastDueTasks());
                break;
            default:
                System.out.println("Unknown display command.");
        }
    }

    private void displayEventsForPeriod(LocalDate referenceDate, ChronoUnit unit) {
        LocalDate startDate, endDate;

        if (unit == ChronoUnit.WEEKS) {
            startDate = referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            endDate = referenceDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        } else if (unit == ChronoUnit.MONTHS) {
            startDate = referenceDate.with(TemporalAdjusters.firstDayOfMonth());
            endDate = referenceDate.with(TemporalAdjusters.lastDayOfMonth());
        } else {
            startDate = referenceDate;
            endDate = referenceDate;
        }

        displayEvents(manager.getEventsForPeriod(startDate, endDate));
    }

    private void displayEvents(List<Event> filteredEvents) {
        if (filteredEvents.isEmpty()) {
            System.out.println("No events to display.");
            return;
        }

        for (Event event : filteredEvents) {
            System.out.println("----------------------------");
            System.out.println("Title: " + event.getTitle());
            System.out.println("Description: " + event.getDescription());
            System.out.println("Date: " + event.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));

            if (event instanceof Appointment) {
                Appointment appointment = (Appointment) event;
                System.out.println("Type: Appointment");
                System.out.println("Start Time: " + appointment.getStartTime().format(DateTimeFormatter.ISO_LOCAL_TIME));
                System.out.println("Duration: " + formatDuration(appointment.getDuration()));
            } else if (event instanceof Task) {
                Task task = (Task) event;
                System.out.println("Type: Task");
                System.out.println("Deadline: " + task.getDeadline().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                System.out.println("Completed: " + (task.isCompleted() ? "Yes" : "No"));
            }
            System.out.println("----------------------------");
        }
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return String.format("%d hours and %d minutes", hours, minutes);
    }
}
