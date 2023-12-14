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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ICalHandler {

    public List<Event> readFromICalFile(String filePath) {
        List<Event> events = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            String title = "", description = "";
            LocalDate date = null;
            LocalTime startTime = null;
            Duration duration = null;
            LocalDateTime startDateTime = null; // For calculating duration

            for (String line : lines) {
                if (line.startsWith("BEGIN:VEVENT")) {
                    title = "";
                    description = "";
                    date = null;
                    startTime = null;
                    duration = null;
                    startDateTime = null;
                } else if (line.startsWith("END:VEVENT")) {
                    if (startTime != null) {
                        // Create an appointment if start time is present
                        Appointment appointment = new Appointment(title, description, date, startTime, duration);
                        events.add(appointment);
                        System.out.println("Added Event: " + title + ", Date: " + date + (startTime != null ? ", StartTime: " + startTime : "") + (duration != null ? ", Duration: " + duration : ""));
                    } else {
                        // Create a task otherwise
                        Task task = new Task(title, description, date, null);
                        events.add(task);
                        System.out.println("Added Event: " + title + ", Date: " + date);
                    }
                } else if (line.startsWith("SUMMARY:")) {
                    title = line.substring(8);
                } else if (line.startsWith("DESCRIPTION:")) {
                    description = line.substring(13);
                } else if (line.startsWith("DTSTART:")) {
                    String dateTimeString = line.substring(8);
                    if (dateTimeString.contains("T")) {
                        startDateTime = parseDateTime(dateTimeString);
                        date = startDateTime.toLocalDate();
                        startTime = startDateTime.toLocalTime();
                    } else {
                        date = LocalDate.parse(dateTimeString, DateTimeFormatter.ofPattern("yyyyMMdd"));
                    }
                } else if (line.startsWith("DTEND:")) {
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
                String icalEvent = eventToICalFormat(event);
                writer.write(icalEvent);
            }

            writer.write("END:VCALENDAR\n");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the iCal file: " + e.getMessage());
        }
    }


    public void updateCalendar(String icalFilePath, EventManager manager) {
        List<Event> events = readFromICalFile(icalFilePath); // Read existing events
        Scanner scanner = new Scanner(System.in);
        boolean moreEvents = true;

        while (moreEvents) {
            System.out.println("Adding a new event.");

            System.out.print("Enter event type (appointment/task): ");
            String eventType = scanner.nextLine().toLowerCase();

            System.out.print("Enter title: ");
            String title = scanner.nextLine();

            System.out.print("Enter description: ");
            String description = scanner.nextLine();

            System.out.print("Enter date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(scanner.nextLine());

            Event event;
            if ("appointment".equals(eventType)) {
                System.out.print("Enter start time (HH:MM): ");
                LocalTime startTime = LocalTime.parse(scanner.nextLine());

                System.out.print("Enter duration in minutes: ");
                long durationMinutes = scanner.nextLong();
                scanner.nextLine(); // consume newline

                event = new Appointment(title, description, date, startTime, Duration.ofMinutes(durationMinutes));
            } else { // assuming task
                System.out.print("Enter deadline (YYYY-MM-DDTHH:MM): ");
                LocalDateTime deadline = LocalDateTime.parse(scanner.nextLine());
                event = new Task(title, description, date, deadline);
            }

            events.add(event);

            System.out.print("Do you want to add another event? (yes/no): ");
            String response = scanner.nextLine();
            moreEvents = "yes".equalsIgnoreCase(response);
        }

        writeToICalFile(icalFilePath, events); // Save new list of events back to the file
        System.out.println("Calendar updated successfully.");
    }


    public String eventToICalFormat(Event event) {
        StringBuilder builder = new StringBuilder();

        builder.append("BEGIN:VEVENT\n");
        builder.append("SUMMARY:").append(event.getTitle()).append("\n");
        builder.append("DESCRIPTION:").append(event.getDescription()).append("\n");
        builder.append("DTSTART:").append(convertToICalDateTimeFormat(event.getDate())).append("\n");

        // Handle end time or duration for Appointment
        if (event instanceof Appointment) {
            Appointment appointment = (Appointment) event;
            LocalDateTime endDateTime = LocalDateTime.of(appointment.getDate(), appointment.getStartTime()).plus(appointment.getDuration());
            builder.append("DTEND:").append(convertToICalDateTimeFormat(endDateTime.toLocalDate())).append("\n");
        }

        // Handle deadline for Task
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
            return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));
        } catch (DateTimeParseException e) {
            // Handle alternative date formats or throw an exception
            System.out.println("Failed to parse date-time: " + dateTimeString);
            throw e;
        }
    }


}