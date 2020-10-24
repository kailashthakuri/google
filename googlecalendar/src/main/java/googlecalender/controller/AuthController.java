package googlecalender.controller;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import googlecalender.dto.GoogleToken;
import googlecalender.service.CredentialsService;
import googlecalender.service.TokenService;
import googlecalender.utils.CredentialUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final static Log logger = LogFactory.getLog(AuthController.class);

    private static final String APPLICATION_NAME = "";
    private HttpTransport httpTransport;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static com.google.api.services.calendar.Calendar client;

    String redirectURI = "http://localhost:8090/auth/google";

    GoogleClientSecrets clientSecrets;
    GoogleAuthorizationCodeFlow flow;
    Credential credential;
//
//    @Value("${google.client.client-id}")
//    private String clientId;
//    @Value("${google.client.client-secret}")
//    private String clientSecret;
//    @Value("${google.client.redirectUri}")
//    private String redirectURI;

    @Autowired
    private CredentialsService credentialsService;

    private Set<Event> events = new HashSet<>();

    final DateTime date1 = new DateTime("2017-05-05T16:30:00.000+05:30");
//    public static String refreshToken = "";
    final DateTime date2 = new DateTime(new Date());

    @Autowired
    private TokenService tokenService;

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    @RequestMapping(value = "/google", method = RequestMethod.GET)
    public RedirectView googleConnectionStatus(HttpServletRequest request) throws Exception {
        GoogleToken token = tokenService.getToken("kshahi");
        if (null == token.getRefreshToken() || token.getRefreshToken().isEmpty()) {
            return new RedirectView(authorize());
        } else {
            return new RedirectView("/auth/success");
        }
    }


    private Credential getCredentialsFromToken(GoogleToken googleToken, String clientId, String clientSecret) throws IOException {
        HttpTransport transport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        return new GoogleCredential.Builder()
                .setClientSecrets(clientId, clientSecret)
                .setJsonFactory(jsonFactory).setTransport(transport).build()
                .setAccessToken(googleToken.getToken())
                .setRefreshToken(googleToken.getRefreshToken());
    }

    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public ResponseEntity<String> success(HttpServletRequest request) throws Exception {
        String message;
        try {
            GoogleToken token = tokenService.getToken("kshahi");
            GoogleClientSecrets clientSecrets = this.credentialsService.getClientSecrets();
            String newToken = CredentialUtils.getNewToken(token.getRefreshToken(), clientSecrets.getWeb().getClientId(), clientSecrets.getWeb().getClientSecret());
            token.setToken(newToken);
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Credential credentialsFromToken = getCredentialsFromToken(token, clientSecrets.getWeb().getClientId(), clientSecrets.getWeb().getClientSecret());
            client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credentialsFromToken)
                    .setApplicationName(APPLICATION_NAME).build();
            message = getEvents(client);
        } catch (Exception e) {
            logger.warn("Exception while handling OAuth2 callback (" + e.getMessage() + ")."
                    + " Redirecting to google connection status page.");
            message = "Exception while handling OAuth2 callback (" + e.getMessage() + ")."
                    + " Redirecting to google connection status page.";
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }


    private String authorize() throws Exception {
        AuthorizationCodeRequestUrl authorizationUrl;
        if (flow == null) {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, this.credentialsService.getClientSecrets(),
                    Collections.singleton(CalendarScopes.CALENDAR))
                    .setAccessType("offline")
                    .setApprovalPrompt("force")
                    .build();
        }
        authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectURI);
        System.out.println("cal authorizationUrl->" + authorizationUrl);
        return authorizationUrl.build();
    }

    @RequestMapping(value = "/google", method = RequestMethod.GET, params = "code")
    public ResponseEntity<String> oauth2Callback(@RequestParam(value = "code") String code) {
        com.google.api.services.calendar.model.Events eventList;
        String message;
        String userId = "kshahi";
        try {
            String redirectURI = "http://localhost:8090/auth/google";
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectURI).execute();
            credential = flow.createAndStoreCredential(response, "userID");
            if (null != credential.getRefreshToken()) {
                this.tokenService.updateToken(new GoogleToken(credential.getAccessToken(), credential.getRefreshToken(), userId));
            }
            client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME).build();
            message = getEvents(client);

        } catch (Exception e) {
            logger.warn("Exception while handling OAuth2 callback (" + e.getMessage() + ")."
                    + " Redirecting to google connection status page.");
            message = "Exception while handling OAuth2 callback (" + e.getMessage() + ")."
                    + " Redirecting to google connection status page.";
        }

        System.out.println("cal message:" + message);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    public String getEvents(Calendar calendar) throws IOException {
        com.google.api.services.calendar.model.Events eventList;
        String message;
        Calendar.Events events = calendar.events();
        eventList = events.list("primary").setTimeMin(date1).setTimeMax(date2).execute();
        System.out.println("My:" + eventList.getItems());
        return eventList.getItems().toString();
    }

    public Set<Event> getEvents() throws IOException {
        return this.events;
    }
}
