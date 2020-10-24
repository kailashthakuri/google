package googlecalender.model;

import java.util.List;

public class ClientSecret {
    private String clientId;
    private String clientSecret;
    private List<String> redirectUrls;

    public ClientSecret() {
    }

    public ClientSecret(String clientId, String clientSecret, List<String> redirectUrls) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUrls = redirectUrls;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public List<String> getRedirectUrls() {
        return redirectUrls;
    }

    public void setRedirectUrls(List<String> redirectUrls) {
        this.redirectUrls = redirectUrls;
    }
}
