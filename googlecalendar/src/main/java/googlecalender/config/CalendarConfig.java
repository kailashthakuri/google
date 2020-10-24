package googlecalender.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.zaxxer.hikari.HikariDataSource;
import googlecalender.GoogleCalendarApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class CalendarConfig {


    @Value("Calendar")
    String applicationName;


    @Bean
    public Credential credential() throws IOException {
        InputStream in = GoogleCalendarApplication.class
                .getResourceAsStream("/crested-trainer-293316-3865453700e5.json");
        return GoogleCredential.fromStream(in)
                .createScoped(Collections.singletonList(CalendarScopes.CALENDAR));
    }


    @Bean
    public NetHttpTransport netHttpTransport() throws GeneralSecurityException, IOException {
        return GoogleNetHttpTransport
                .newTrustedTransport();
    }


    @Bean
    public Calendar calendar(NetHttpTransport httpTransport, Credential credential) {
        final JsonFactory JSON_FACTORY = JacksonFactory
                .getDefaultInstance();
        return new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(this.applicationName)
                .build();
    }

    @Bean
    public DataStoreFactory dataStoreFactory() {
        return new MemoryDataStoreFactory();
    }
}
