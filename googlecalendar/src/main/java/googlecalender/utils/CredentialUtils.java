package googlecalender.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import googlecalender.GoogleCalendarApplication;
import googlecalender.model.ClientSecret;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CredentialUtils {


    public static String refreshToken = "";

    private static final String APPLICATION_NAME = "Calendar";
    private static final JsonFactory JSON_FACTORY = JacksonFactory
            .getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials1.json";

    private static Credential getCredentials(
            final NetHttpTransport HTTP_TRANSPORT) throws IOException {
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
        DataStoreFactory storeFactory = new MemoryDataStoreFactory();
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(storeFactory)
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(8090).build();
        return new AuthorizationCodeInstalledApp(flow, receiver)
                .authorize("user");
    }

    public ClientSecret getClientSecret() throws IOException {
        InputStream in = GoogleCalendarApplication.class
                .getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException(
                    "Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets secrets = GoogleClientSecrets
                .load(JSON_FACTORY, new InputStreamReader(in));
        return new ClientSecret(secrets.getWeb().getClientId(), secrets.getWeb().getClientSecret(), secrets.getWeb().getRedirectUris());
    }

    public static Credential getCredentials() throws GeneralSecurityException, IOException, FileNotFoundException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
        // Load client secrets.
        String CREDENTIALS_FILE_PATH = "/client_secret.json"; //OAuth 2.0 clinet credentials json
        InputStream in = GoogleCalendarApplication.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        String clientId = clientSecrets.getDetails().getClientId();
        String clientSecret = clientSecrets.getDetails().getClientSecret();

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(clientId, clientSecret)
                .build();

//        String refreshToken = "<REFRESH-TOKEN>"; //Find a secure way to store and load refresh token
//        credential.setAccessToken(getNewToken(refreshToken, clientId, clientSecret));
//        credential.setRefreshToken(refreshToken);
        return credential;
    }

    public static String getNewToken(String refreshToken, String clientId, String clientSecret) throws IOException {
        ArrayList<String> scopes = new ArrayList<>();
        scopes.add(CalendarScopes.CALENDAR);
        TokenResponse tokenResponse = new GoogleRefreshTokenRequest(new NetHttpTransport(), new JacksonFactory(),
                refreshToken, clientId, clientSecret).setScopes(scopes).setGrantType("refresh_token").execute();
        return tokenResponse.getAccessToken();
    }

    public static String getUpdatedToken(String refreshToken, ClientSecret clientSecret) throws IOException {
        ArrayList<String> scopes = new ArrayList<>();
        scopes.add(CalendarScopes.CALENDAR);
        TokenResponse tokenResponse = new GoogleRefreshTokenRequest(new NetHttpTransport(), new JacksonFactory(),
                refreshToken, clientSecret.getClientId(), clientSecret.getClientSecret()).setScopes(scopes).setGrantType("refresh_token").execute();
        return tokenResponse.getAccessToken();
    }
}
