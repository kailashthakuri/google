package googlecalender.service;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import googlecalender.GoogleCalendarApplication;
import googlecalender.entity.TokenInfo;
import googlecalender.model.ClientSecret;
import googlecalender.utils.CredentialUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

@Service
public class CredentialsService {

    private final static Log logger = LogFactory.getLog(CredentialsService.class);

    @Autowired
    private DataStoreFactory storeFactory;

    NetHttpTransport httpTransport;
    GoogleAuthorizationCodeFlow flow;
    private static final JsonFactory JSON_FACTORY = JacksonFactory
            .getDefaultInstance();

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/client_secret.json";


    public GoogleClientSecrets getClientSecrets() throws IOException {
        InputStream in = GoogleCalendarApplication.class
                .getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException(
                    "Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        return GoogleClientSecrets
                .load(JSON_FACTORY, new InputStreamReader(in));
    }

    public String authorize(TokenInfo tokenInfo) throws Exception {
        AuthorizationCodeRequestUrl authorizationUrl;
        ClientSecret secret = CredentialUtils.getClientSecret();
        if (flow == null) {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, this.getClientSecrets(),
                    Collections.singleton(CalendarScopes.CALENDAR))
                    .setAccessType("offline")
                    .setApprovalPrompt("force")
                    .build();
        }
        authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(secret.getRedirectUrls().get(0));
        logger.info("Authorization URL : " + authorizationUrl);
        return authorizationUrl.build() + "&state=" + tokenInfo.getUserId();
    }

    public String authorize(HttpTransport httpTransport) throws Exception {
        AuthorizationCodeRequestUrl authorizationUrl;
        if (flow == null) {
            InputStream in = GoogleCalendarApplication.class
                    .getResourceAsStream(CREDENTIALS_FILE_PATH);
            if (in == null) {
                throw new FileNotFoundException(
                        "Resource not found: " + CREDENTIALS_FILE_PATH);
            }
            GoogleClientSecrets clientSecrets = GoogleClientSecrets
                    .load(JSON_FACTORY, new InputStreamReader(in));
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
                    Collections.singleton(CalendarScopes.CALENDAR)).build();
        }
        authorizationUrl = flow.newAuthorizationUrl().setRedirectUri("http://localhost:8090/auth/google");
        System.out.println("cal authorizationUrl->" + authorizationUrl);
        return authorizationUrl.build();
    }


    public Credential getCredentials(final NetHttpTransport httpTransport) throws IOException {
        // Load client secrets.
        InputStream in = GoogleCalendarApplication.class
                .getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException(
                    "Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets
                .load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(storeFactory)
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(8090).build();
        return new AuthorizationCodeInstalledApp(flow, receiver)
                .authorize("user");
    }
}
