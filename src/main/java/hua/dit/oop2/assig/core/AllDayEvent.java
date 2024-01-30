package hua.dit.oop2.assig.core;

import java.time.LocalDate;
import gr.hua.dit.oop2.calendar.TimeService;
import gr.hua.dit.oop2.calendar.TimeTeller;

public class AllDayEvent extends Event {

    private static final TimeTeller teller = TimeService.getTeller();

    public AllDayEvent(String title, String description, LocalDate date) {
        super(title, description, date); // Inherits from Event
    }


    // Method to check if the event is occurring today
    public boolean isToday() {
        LocalDate currentDate = teller.now().toLocalDate();
        return getDate().isEqual(currentDate);
    }

}
