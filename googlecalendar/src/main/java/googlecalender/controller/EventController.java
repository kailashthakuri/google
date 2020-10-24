package googlecalender.controller;


import googlecalender.dto.ResponseDTO;
import googlecalender.dto.UserEventDTO;
import googlecalender.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/calendar/events")
public class EventController {

    /**
     * Sample Calendar Id :  "1dteantdh3voi761onf1i6fl3s@group.calendar.google.com" => kailashshahi833@gmail.com
     */

    @Autowired
    private EventService eventService;


    // ?calendarId=1dteantdh3voi761onf1i6fl3s@group.calendar.google.com,
    @GetMapping()
    public ResponseEntity<ResponseDTO> getEvents(@RequestParam("calendarId") String calendarId) {
        return new ResponseEntity(eventService.getEvents(calendarId), HttpStatus.OK);
    }

    /* {
                "calendarId":"1dteantdh3voi761onf1i6fl3s@group.calendar.google.com",
                "id":"1234567890",
                "location":"kathmandu,Nepal",
                "summary":"Order 3",
                "description":"This is just description",
                "startDate":"2020-10-24T15:40:00-07:00",
                "endDate":"2020-10-24T23:40:00-07:00"
     }*/
    @PostMapping()
    ResponseEntity<ResponseDTO> createEvent(@RequestBody UserEventDTO event) {
        return new ResponseEntity<>(eventService.createEvent(event), HttpStatus.OK);
    }

    //    {
    //            "calendarId":"1dteantdh3voi761onf1i6fl3s@group.calendar.google.com",
    //            "id":"12345",
    //    }
    @DeleteMapping()
    ResponseEntity<ResponseDTO> deleteEvent(@RequestBody UserEventDTO event) {
        return new ResponseEntity<>(eventService.deleteEvent(event), HttpStatus.OK);
    }

}
