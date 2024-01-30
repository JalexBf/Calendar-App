package hua.dit.oop2.assig.GUI;

import hua.dit.oop2.assig.core.Event;
import hua.dit.oop2.assig.core.EventManager;
import hua.dit.oop2.assig.GUI.EventListView;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

public class EventListController {
    private EventManager eventManager;
    private EventListView eventListView;

    public EventListController(EventListView eventListView) {
        this.eventManager = EventManager.getInstance();
        this.eventListView = eventListView;
    }

    public void showAllEvents() {
        List<Event> allEvents = eventManager.getAllEvents();
        eventListView.updateEventList(allEvents);
    }

    public void showFutureEvents() {
        LocalDate now = LocalDate.now();
        Predicate<Event> futureEventCondition = event -> event.getDate().isAfter(now);
        List<Event> futureEvents = eventManager.getEventsByCondition(futureEventCondition);
        eventListView.updateEventList(futureEvents);
    }

    public void showPastEvents() {
        LocalDate now = LocalDate.now();
        Predicate<Event> pastEventCondition = event -> event.getDate().isBefore(now);
        List<Event> pastEvents = eventManager.getEventsByCondition(pastEventCondition);
        eventListView.updateEventList(pastEvents);
    }

}
