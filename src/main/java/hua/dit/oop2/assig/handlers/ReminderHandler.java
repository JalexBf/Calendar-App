package hua.dit.oop2.assig.handlers;

import gr.hua.dit.oop2.calendar.TimeEvent;
import gr.hua.dit.oop2.calendar.TimeListener;
import gr.hua.dit.oop2.calendar.TimeService;
import gr.hua.dit.oop2.calendar.TimeTeller;
import hua.dit.oop2.assig.core.Event;
import hua.dit.oop2.assig.core.EventManager;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ReminderHandler implements TimeListener {
    private Set<String> notifiedEvents;
    private final int REMINDER_WINDOW_MINUTES = 5; // Reminder window in minutes

    public ReminderHandler() {
        this.notifiedEvents = new HashSet<>();
        TimeTeller teller = TimeService.getTeller();
        teller.addTimeListener(this);
    }


    @Override
    public void timeChanged(TimeEvent e) {
        checkForUpcomingEvents(e.getDateTime());
    }


    private void checkForUpcomingEvents(LocalDateTime currentTime) {
        List<Event> events = EventManager.getInstance().getAllEvents();
        for (Event event : events) {
            LocalDateTime eventDateTime = LocalDateTime.of(event.getDate(), event.getStartTime());
            if (eventDateTime.isAfter(currentTime) && eventDateTime.isBefore(currentTime.plusMinutes(REMINDER_WINDOW_MINUTES))) {
                if (!notifiedEvents.contains(event.getUuid())) {
                    // Trigger the reminder for the event
                    System.out.println("Reminder: You have an upcoming event - " + event.getTitle());
                    notifiedEvents.add(event.getUuid()); // Mark this event as notified
                }
            }
        }
    }
}
