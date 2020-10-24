package googlecalender.service;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import googlecalender.dto.ResponseDTO;
import googlecalender.dto.UserEventDTO;
import googlecalender.utils.EventUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EventService {


    @Autowired
    private Calendar calendar;

    public ResponseDTO getEvents(String calendarId) {
        DateTime now = new DateTime(System.currentTimeMillis());
        List<Event> items = new ArrayList<>();
        try {
            Events events = calendar.events().list(calendarId)
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            items = events.getItems();
            if (items.isEmpty()) {
                System.out.println("No upcoming events found.");
            } else {
                System.out.println("Upcoming events");
                for (Event event : items) {
                    DateTime start = event.getStart().getDateTime();
                    if (start == null) {
                        start = event.getStart().getDate();
                    }
                    System.out.printf("%s (%s)\n", event.getSummary(), start);
                }
            }
        } catch (IOException exception) {
            System.out.println("Something wend wrong!");
        }
        return new ResponseDTO(items);
    }

    public ResponseDTO deleteEvent(UserEventDTO userEvent) {
        try {
            calendar.events().delete(userEvent.getCalendarId(), userEvent.getId()).execute();
            return new ResponseDTO(userEvent);
        } catch (IOException exception) {
            GoogleJsonError jsonError = ((GoogleJsonResponseException) exception).getDetails();
            System.out.println(jsonError);
        }
        return new ResponseDTO(null);
    }

    public String getEvents(Calendar calendar) throws IOException {
        final DateTime date1 = new DateTime("2017-05-05T16:30:00.000+05:30");
        final DateTime date2 = new DateTime(new Date());
        com.google.api.services.calendar.model.Events eventList;
        String message;
        Calendar.Events events = calendar.events();
        eventList = events.list("primary").setTimeMin(date1).setTimeMax(date2).execute();
        System.out.println("My:" + eventList.getItems());
        return eventList.getItems().toString();
    }

    public ResponseDTO createEvent(UserEventDTO userEvent) {
        Event newEvent = new Event()
                .setSummary(userEvent.getSummary())
                .setLocation(userEvent.getLocation())
                .setDescription(userEvent.getDescription())
                .setId(userEvent.getId());
        newEvent.setStart(EventUtils.getDate(userEvent.getStartDate()));
        newEvent.setEnd(EventUtils.getDate(userEvent.getEndDate()));
        newEvent.setRecurrence(EventUtils.getRecurrence());
//        newEvent.setAttendees(EventUtils.getAttendee());
        newEvent.setReminders(EventUtils.getReminder());
        try {
            newEvent = calendar.events().insert(userEvent.getCalendarId(), newEvent).execute();
            System.out.printf("Event created: %s\n", newEvent.getHtmlLink());
            return new ResponseDTO(newEvent);
        } catch (IOException exception) {
            GoogleJsonError jsonError = ((GoogleJsonResponseException) exception).getDetails();
            System.out.println(jsonError);
        }
        return new ResponseDTO(null);
    }

}
