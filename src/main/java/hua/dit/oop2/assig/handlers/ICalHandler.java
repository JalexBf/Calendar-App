package hua.dit.oop2.assig.handlers;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import gr.hua.dit.oop2.calendar.TimeService;
import gr.hua.dit.oop2.calendar.TimeTeller;
import hua.dit.oop2.assig.core.AllDayEvent;
import hua.dit.oop2.assig.core.Appointment;
import hua.dit.oop2.assig.core.Event;
import hua.dit.oop2.assig.core.Task;

public class ICalHandler {

    private static final TimeTeller teller = TimeService.getTeller();


    public List<Event> readFromICalFile(String filePath) throws IOException{
        List<Event> events = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            StringBuilder descriptionBuilder = new StringBuilder();
            String title = "";
            String uuid = null;
            LocalDate date = null;
            LocalTime startTime = null;
            boolean isAllDay = false;
            boolean isReadingDescription = false;
            LocalDate dueDate = null; // For VTODO items
            boolean isTaskCompleted = false; // For task completion status

            for (String line : lines) {
                if (line.startsWith("BEGIN:VEVENT") || line.startsWith("BEGIN:VTODO")) {
                    // Reset variables for new event or task
                    descriptionBuilder.setLength(0);
                    title = "";
                    uuid = null;
                    date = null;
                    startTime = null;
                    isAllDay = false;
                    isReadingDescription = false;
                    dueDate = null;
                    isTaskCompleted = false;
                } else if (line.startsWith("UID:")) {
                    uuid = line.substring(4).trim();
                } else if (line.startsWith("STATUS:")) {
                    isTaskCompleted = line.substring(7).trim().equalsIgnoreCase("COMPLETED");
                } else if (line.startsWith("END:VEVENT")) {
                    String description = descriptionBuilder.toString();
                    Event event = isAllDay ? new AllDayEvent(title, description, date) : new Event(title, description, date, startTime);
                    if (uuid != null) {
                        event.setUuid(uuid);
                    }
                    events.add(event);
                } else if (line.startsWith("END:VTODO")) {
                    String description = descriptionBuilder.toString();
                    LocalDateTime deadline = (dueDate != null) ? dueDate.atStartOfDay() : null;
                    Task task = new Task(title, description, deadline);
                    task.setUuid(uuid);
                    task.setCompleted(isTaskCompleted);
                    events.add(task);
                } else if (line.startsWith("SUMMARY:")) {
                    title = parseSummaryLine(line);
                } else if (line.startsWith("DESCRIPTION:")) {
                    descriptionBuilder = new StringBuilder(line.substring(12).trim());
                    isReadingDescription = true;
                } else if (isReadingDescription && (line.startsWith(" ") || line.startsWith("\t"))) {
                    descriptionBuilder.append(" ").append(line.trim());
                } else if (line.startsWith("DTSTART;VALUE=DATE:")) {
                    date = LocalDate.parse(line.substring(19), DateTimeFormatter.BASIC_ISO_DATE);
                    isAllDay = true;
                } else if (line.startsWith("DTSTART:")) {
                    LocalDateTime dateTime = LocalDateTime.parse(line.substring(8), DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
                    date = dateTime.toLocalDate();
                    startTime = dateTime.toLocalTime();
                } else if (line.startsWith("DUE:")) {
                    try {
                        int colonIndex = line.indexOf(':');
                        if (colonIndex != -1) {
                            String dueDateTimeString = line.substring(colonIndex + 1);
                            LocalDateTime dueDateTime = LocalDateTime.parse(dueDateTimeString, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
                            dueDate = dueDateTime.toLocalDate();
                        }
                    } catch (DateTimeParseException e) {
                        System.out.println("Failed to parse DUE date and time: " + e.getMessage());
                        dueDate = null;
                    }
                } else if (startsWithKnownProperty(line)) {
                    isReadingDescription = false;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading iCal file: " + e.getMessage());
        }
        return events;
    }


    private String parseSummaryLine(String line) {
        int colonIndex = line.indexOf(':');
        if (colonIndex != -1 && colonIndex < line.length() - 1) {
            return line.substring(colonIndex + 1).trim();
        }
        return "No title";
    }


    private boolean startsWithKnownProperty(String line) {
        String[] knownProperties = {"DTSTART;", "DTSTART:", "DTEND;", "DTEND:", "UID:", "CLASS:", "CREATED:", "URL:", "LOCATION:", "SUMMARY:", "DESCRIPTION:", "DUE;"};
        for (String prop : knownProperties) {
            if (line.startsWith(prop)) {
                return true;
            }
        }
        return false;
    }


    public void writeToICalFile(String filePath, List<Event> updatedEvents) throws IOException{
        try (FileWriter writer = new FileWriter(filePath, false)) {
            writer.write("BEGIN:VCALENDAR\n");
            writer.write("VERSION:2.0\n");
            writer.write("PRODID:-//Your Company//Your Product//EN\n");

            for (Event event : updatedEvents) {
                String icalEvent = eventToICalFormat(event);
                writer.write(icalEvent);
            }

            writer.write("END:VCALENDAR\n");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the iCal file: " + e.getMessage());
        }
    }


    public String eventToICalFormat(Event event) {
        StringBuilder builder = new StringBuilder();

        if (event instanceof Appointment) {
            // Handle formatting for Appointment
            Appointment appointment = (Appointment) event;
            LocalDateTime startDateTime = LocalDateTime.of(appointment.getDate(), appointment.getStartTime());
            LocalDateTime endDateTime = LocalDateTime.of(appointment.getDate(), appointment.getEndTime());

            builder.append("BEGIN:VEVENT\n")
                    .append("UID:").append(event.getUuid()).append("\n")
                    .append("CLASS:PUBLIC\n")
                    .append("SUMMARY:").append(appointment.getTitle()).append("\n")
                    .append("DESCRIPTION:").append(appointment.getDescription().replace("\n", "\\n")).append("\n")
                    .append("DTSTART:").append(convertToICalDateTimeFormat(startDateTime)).append("\n")
                    .append("DTEND:").append(convertToICalDateTimeFormat(endDateTime)).append("\n")
                    .append("END:VEVENT\n");
        } else if (event instanceof Task) {
            // Handle formatting for Task
            Task task = (Task) event;
            LocalDateTime dueDateTime = task.getDeadline();

            builder.append("BEGIN:VTODO\n")
                    .append("UID:").append(task.getUuid()).append("\n")
                    .append("CLASS:PUBLIC\n")
                    .append("SUMMARY:").append(task.getTitle()).append("\n")
                    .append("DESCRIPTION:").append(task.getDescription().replace("\n", "\\n")).append("\n")
                    .append("DUE:").append(convertToICalDateTimeFormat(dueDateTime)).append("\n")
                    .append("STATUS:").append(task.isCompleted() ? "COMPLETED" : "NEEDS-ACTION").append("\n")
                    .append("END:VTODO\n");
        } else {
            // Handle formatting for general Event
            Event generalEvent = event;
            LocalDateTime eventDateTime = generalEvent.isAllDay() ? generalEvent.getDate().atStartOfDay() : LocalDateTime.of(generalEvent.getDate(), generalEvent.getStartTime());

            builder.append("BEGIN:VEVENT\n")
                    .append("UID:").append(generalEvent.getUuid()).append("\n")
                    .append("CLASS:PUBLIC\n")
                    .append("SUMMARY:").append(generalEvent.getTitle()).append("\n")
                    .append("DESCRIPTION:").append(generalEvent.getDescription().replace("\n", "\\n")).append("\n")
                    .append(generalEvent.isAllDay() ? "DTSTART;VALUE=DATE:" : "DTSTART:")
                    .append(convertToICalDateTimeFormat(eventDateTime)).append("\n")
                    .append("END:VEVENT\n");
        }

        return builder.toString();
    }

    private String convertToICalDateTimeFormat(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
    }


    private String convertToICalDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return date.format(formatter);
    }

}
