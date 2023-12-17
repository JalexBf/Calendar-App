package hua.dit.oop2.assig;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import gr.hua.dit.oop2.calendar.TimeService;
import gr.hua.dit.oop2.calendar.TimeTeller;
import hua.dit.oop2.assig.core.AllDayEvent;
import hua.dit.oop2.assig.core.Event;

public class ICalHandler {

    private static final TimeTeller teller = TimeService.getTeller();

    public List<Event> readFromICalFile(String filePath) {
        List<Event> events = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            StringBuilder descriptionBuilder = new StringBuilder();
            String title = "";
            LocalDate date = null;
            LocalTime startTime = null;
            boolean isAllDay = false;
            boolean isReadingDescription = false;

            for (String line : lines) {
                if (line.startsWith("BEGIN:VEVENT")) {
                    descriptionBuilder.setLength(0);
                    title = "";
                    date = null;
                    startTime = null;
                    isAllDay = false;
                    isReadingDescription = false;
                } else if (line.startsWith("END:VEVENT")) {
                    String description = descriptionBuilder.toString();
                    if (isAllDay) {
                        events.add(new AllDayEvent(title, description, date));
                    } else {
                        events.add(new Event(title, description, date, startTime));
                    }
                } else if (line.startsWith("SUMMARY:")) {
                    title = line.substring(8).trim();
                } else if (line.startsWith("DESCRIPTION:")) {
                    descriptionBuilder = new StringBuilder(line.substring(12).trim());
                    isReadingDescription = true;
                } else if (isReadingDescription && (line.startsWith(" ") || line.startsWith("\t"))) {
                    descriptionBuilder.append(" ").append(line.trim());
                } else if (line.startsWith("DTSTART;VALUE=DATE:")) {
                    date = LocalDate.parse(line.substring(19), DateTimeFormatter.BASIC_ISO_DATE);
                    isAllDay = true;
                } else if (line.startsWith("DTSTART:")) {
                    String dateTimeString = line.substring(8);
                    LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
                    date = dateTime.toLocalDate();
                    startTime = dateTime.toLocalTime();
                } else if (startsWithKnownProperty(line)) {
                    isReadingDescription = false;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading iCal file: " + e.getMessage());
        }
        System.out.println("Total events parsed: " + events.size());
        return events;
    }

    private boolean startsWithKnownProperty(String line) {
        String[] knownProperties = {"DTSTART;", "DTSTART:", "DTEND;", "DTEND:", "UID:", "CLASS:", "CREATED:", "URL:", "LOCATION:", "SUMMARY:", "DESCRIPTION:"};
        for (String prop : knownProperties) {
            if (line.startsWith(prop)) {
                return true;
            }
        }
        return false;
    }

    public void writeToICalFile(String filePath, List<Event> newEvents) {
        List<Event> existingEvents = readFromICalFile(filePath);
        existingEvents.addAll(newEvents);

        try (FileWriter writer = new FileWriter(filePath, false)) {
            writer.write("BEGIN:VCALENDAR\n");
            writer.write("VERSION:2.0\n");
            writer.write("PRODID:-//Your Company//Your Product//EN\n");

            for (Event event : existingEvents) {
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
        builder.append("BEGIN:VEVENT\n");
        builder.append("CLASS:PUBLIC\n");
        builder.append("CREATED:").append(teller.now().format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"))).append("\n");
        builder.append("SUMMARY:").append(event.getTitle()).append("\n");
        builder.append("DESCRIPTION:").append(event.getDescription().replace("\n", "\\n")).append("\n");

        if (event instanceof AllDayEvent) {
            builder.append("DTSTART;VALUE=DATE:").append(convertToICalDate(event.getDate())).append("\n");
        } else {
            builder.append("DTSTART:").append(convertToICalDateTimeFormat(event.getDate(), (event.getStartTime() != null) ? event.getStartTime() : LocalTime.MIDNIGHT)).append("\n");
        }

        builder.append("END:VEVENT\n");
        return builder.toString();
    }

    private String convertToICalDate(LocalDate date) {
        return DateTimeFormatter.ofPattern("yyyyMMdd").format(date);
    }

    private String convertToICalDateTimeFormat(LocalDate date, LocalTime time) {
        return DateTimeFormatter.ofPattern("yyyyMMdd").format(date) + "T" + DateTimeFormatter.ofPattern("HHmmss").format(time) + "Z";
    }
}
