package googlecalender.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import googlecalender.dto.ErrorDTO;
import googlecalender.dto.GoogleToken;
import googlecalender.entity.TokenInfo;
import googlecalender.exception.CalendarException;
import googlecalender.model.ClientSecret;
import googlecalender.service.CredentialsService;
import googlecalender.service.EventService;
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


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final static Log logger = LogFactory.getLog(AuthController.class);

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "";
    private HttpTransport httpTransport;
    private static com.google.api.services.calendar.Calendar client;

    GoogleClientSecrets clientSecrets;
    GoogleAuthorizationCodeFlow flow;
    Credential credential;

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private EventService eventService;

    @Autowired
    private TokenService tokenService;

    @RequestMapping(value = "/google", method = RequestMethod.GET)
    public RedirectView googleConnectionStatus(HttpServletRequest request, @RequestParam String userId) throws Exception {
        TokenInfo tokenInfo = tokenService.getTokenInfo(userId);
        if (null == tokenInfo.getRefreshToken() || tokenInfo.getRefreshToken().isEmpty()) {
            return new RedirectView(this.credentialsService.authorize(tokenInfo));
        } else {
            return new RedirectView("/auth/success?state=" + tokenInfo.getUserId());
        }
    }


    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public ResponseEntity<String> success(HttpServletRequest request, @RequestParam String state) {
        String message;
        try {
            TokenInfo tokenInfo = tokenService.getTokenInfo(state);
            String newToken = CredentialUtils.getUpdatedToken(tokenInfo.getRefreshToken(), CredentialUtils.getClientSecret());
            tokenInfo.setToken(newToken);
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            ClientSecret clientSecret = CredentialUtils.getClientSecret();
            Credential credentialsFromToken = CredentialUtils.getCredentialsFromToken(tokenInfo, clientSecret);
            client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credentialsFromToken)
                    .setApplicationName(APPLICATION_NAME).build();
            message = this.eventService.getEvents(client);
        } catch (Exception e) {
            logger.error("Exception while handling OAuth2 callback (" + e.getMessage() + ")."
                    + " Redirecting to google connection status page.");
            throw new CalendarException(new ErrorDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Messagae"));
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }


    @RequestMapping(value = "/google", method = RequestMethod.GET, params = "code")
    public ResponseEntity<String> oauth2Callback(@RequestParam(value = "code") String code) {
        com.google.api.services.calendar.model.Events eventList;
        String message;
        String userId = "kshahi";
        try {
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(CredentialUtils.getClientSecret().getRedirectUrls().get(0)).execute();
            credential = flow.createAndStoreCredential(response, "userID");
            if (null != credential.getRefreshToken()) {
                this.tokenService.updateToken(new GoogleToken(credential.getAccessToken(), credential.getRefreshToken(), userId));
            }
            client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME).build();
            message = this.eventService.getEvents(client);

        } catch (Exception e) {
            logger.warn("Exception while handling OAuth2 callback (" + e.getMessage() + ")."
                    + " Redirecting to google connection status page.");
            throw new CalendarException(new ErrorDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Messagae"));
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
