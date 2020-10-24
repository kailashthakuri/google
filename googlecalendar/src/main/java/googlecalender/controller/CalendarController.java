package googlecalender.controller;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import googlecalender.dto.ResponseDTO;
import googlecalender.service.CalendarService;
import googlecalender.service.CredentialsService;
import googlecalender.utils.CredentialUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/calendar")
public class CalendarController {
    /**
     * Sample Calendar Id :  "1dteantdh3voi761onf1i6fl3s@group.calendar.google.com" => kailashshahi833@gmail.com
     */
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private CredentialsService credentialsService;


    @GetMapping()
    public ResponseEntity<ResponseDTO> getEvents() throws GeneralSecurityException, IOException {
//        System.out.println(CredentialUtils.getCredentials());
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        System.out.println(credentialsService.getCredentials(HTTP_TRANSPORT));
        return new ResponseEntity(new ResponseDTO(null), HttpStatus.OK);
    }

}
