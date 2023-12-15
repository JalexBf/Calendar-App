package hua.dit.oop2.assig;

import java.time.LocalDate;

public class AllDayEvent extends Event {

    public AllDayEvent(String title, String description, LocalDate date) {
        super(title, description, date);
    }

    // This method provides a string representation of the all-day event
    @Override
    public String toString() {
        return "AllDayEvent{" +
                "Title='" + getTitle() + '\'' +
                ", Description='" + getDescription() + '\'' +
                ", Date=" + getDate() +
                '}';
    }
}
