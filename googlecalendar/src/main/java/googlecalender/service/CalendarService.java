package googlecalender.service;

import com.google.api.services.calendar.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CalendarService {

    @Autowired
    private Calendar calendar;

    private static com.google.api.services.calendar.model.Calendar createNewCalendar(
            Calendar service)
            throws IOException {
        /*Create a new calendar*/
        com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
        calendar.setSummary("calendarSummary");
        calendar.setTimeZone("Asia/Kathmandu");
        calendar.setId("calendarSummary");

        com.google.api.services.calendar.model.Calendar calendar1 = new com.google.api.services.calendar.model.Calendar();
        calendar1.setSummary("calendar1Summary");
        calendar1.setTimeZone("Asia/Kathmandu");
        calendar.setId("calendar1Summary");

        //         Insert the new calendar
        com.google.api.services.calendar.model.Calendar createdCalendar = service
                .calendars().insert(calendar).execute();

        com.google.api.services.calendar.model.Calendar createdCalendar1 = service
                .calendars().insert(calendar1).execute();

        System.out.println(createdCalendar.getSummary());
        System.out.println(createdCalendar1.getSummary());
        /*Create a new calendar*/
        return createdCalendar;
    }


}
