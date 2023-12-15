package hua.dit.oop2.assig;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ICalHandler {

    public List<Event> readFromICalFile(String filePath) {
        List<Event> events = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            StringBuilder descriptionBuilder = new StringBuilder();
            String title = "Default Title"; // Default title or another logic
            LocalDate date = null;
            LocalTime startTime = null;
            Duration duration = null;
            LocalDateTime startDateTime = null;
            boolean isAllDay = false;

            for (String line : lines) {
                if (line.startsWith("BEGIN:VEVENT")) {
                    descriptionBuilder.setLength(0); // Reset the description builder
                    title = "Default Title"; // Reset title for each event
                    // ... other initializations
                } else if (line.startsWith("END:VEVENT")) {
                    String description = descriptionBuilder.toString();
                    Event event;
                    if (isAllDay) {
                        event = new AllDayEvent(title, description, date);
                    } else {
                        event = new Appointment(title, description, date, startTime, duration);
                    }
                    events.add(event);

            } else if (line.startsWith("DESCRIPTION:")) {
                    descriptionBuilder.append(line.substring(12));
                } else if (line.startsWith(" ") || line.startsWith("\t")) { // Continuation of description
                    descriptionBuilder.append(line.trim());
                } else if (line.startsWith("DTSTART;VALUE=DATE:")) {
                    String dateString = line.substring(19);
                    date = LocalDate.parse(dateString, DateTimeFormatter.BASIC_ISO_DATE);
                    isAllDay = true;
                } else if (line.startsWith("DTSTART:")) {
                    String dateTimeString = line.substring(8);
                    startDateTime = parseDateTime(dateTimeString);
                    date = startDateTime.toLocalDate();
                    startTime = startDateTime.toLocalTime();
                } else if (line.startsWith("DTEND:") && !isAllDay) {
                    String dateTimeString = line.substring(6);
                    if (dateTimeString.contains("T") && startDateTime != null) {
                        LocalDateTime endDateTime = parseDateTime(dateTimeString);
                        duration = Duration.between(startDateTime, endDateTime);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading iCal file: " + e.getMessage());
        }
        return events;
    }


    public void writeToICalFile(String filePath, List<Event> events) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("BEGIN:VCALENDAR\n");
            writer.write("VERSION:2.0\n");
            writer.write("PRODID:-//Your Company//Your Product//EN\n");

            for (Event event : events) {
                if (event instanceof AllDayEvent) {
                    AllDayEvent allDayEvent = (AllDayEvent) event;
                    writer.write("BEGIN:VEVENT\n");
                    writer.write("SUMMARY:" + allDayEvent.getTitle() + "\n");
                    writer.write("DTSTART;VALUE=DATE:" + allDayEvent.getDate().format(DateTimeFormatter.BASIC_ISO_DATE) + "\n");
                    writer.write("DTEND;VALUE=DATE:" + allDayEvent.getDate().plusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE) + "\n");
                    writer.write("END:VEVENT\n");
                } else {
                    String icalEvent = eventToICalFormat(event);
                    writer.write(icalEvent);
                }
            }

            writer.write("END:VCALENDAR\n");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the iCal file: " + e.getMessage());
        }
    }

    public String eventToICalFormat(Event event) {
        StringBuilder builder = new StringBuilder();

        builder.append("BEGIN:VEVENT\n");
        builder.append("SUMMARY:").append(event.getTitle()).append("\n");
        builder.append("DESCRIPTION:").append(event.getDescription()).append("\n");
        builder.append("DTSTART:").append(convertToICalDateTimeFormat(event.getDate())).append("\n");

        if (event instanceof Appointment) {
            Appointment appointment = (Appointment) event;
            LocalDateTime endDateTime = LocalDateTime.of(appointment.getDate(), appointment.getStartTime()).plus(appointment.getDuration());
            builder.append("DTEND:").append(convertToICalDateTimeFormat(endDateTime.toLocalDate())).append("\n");
        }

        if (event instanceof Task) {
            Task task = (Task) event;
            builder.append("DUE:").append(convertToICalDateTimeFormat(task.getDeadline().toLocalDate())).append("\n");
        }

        builder.append("END:VEVENT\n");
        return builder.toString();
    }

    public String convertToICalDateTimeFormat(LocalDate date) {
        return DateTimeFormatter.ofPattern("yyyyMMdd").format(date) + "T" + DateTimeFormatter.ofPattern("HHmmss").format(LocalTime.MIDNIGHT) + "Z";
    }

    private LocalDateTime parseDateTime(String dateTimeString) {
        try {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendPattern("yyyyMMdd'T'HHmmss")
                    .optionalStart()
                    .appendLiteral('Z')
                    .optionalEnd()
                    .toFormatter();

            return LocalDateTime.parse(dateTimeString, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Failed to parse date-time: " + dateTimeString);
            throw e;
        }
    }
}
