package googlecalender.utils;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import java.util.Arrays;
import java.util.List;

public class EventUtils {

    public static EventDateTime getDate(String date) {
        return new EventDateTime()
                .setDateTime(new DateTime(date))
                .setTimeZone("Asia/Kathmandu");
    }

    public static List<String> getRecurrence() {
        String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=2"};
        return Arrays.asList(recurrence);
    }

    public static List<EventAttendee> getAttendee() {
        EventAttendee[] attendee = new EventAttendee[]{
                new EventAttendee().setEmail("kailashthakuri833@gmail.com")
        };
        return Arrays.asList(attendee);
    }

    public static Event.Reminders getReminder() {
        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        return new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
    }

}
