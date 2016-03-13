package uk.co.optimisticpanda.duoauth;

import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static javax.ws.rs.HttpMethod.POST;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.HttpHeaders.DATE;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.glassfish.jersey.client.authentication.HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD;
import static org.glassfish.jersey.client.authentication.HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DuoGateway {
    private static final String AUTH_PATH = "/auth/v2/auth";
    private static Logger L = LoggerFactory.getLogger(DuoGateway.class);
    private final Client client;
    private final String key;
    private final String secret;
    private final String hostName;

    public DuoGateway(Client client, String hostName, String key, String secret) {
        this.client = client;
        this.client.register(HttpAuthenticationFeature.basicBuilder().build());
        this.client.register(JacksonFeature.class);
        this.key = key;
        this.secret = secret;
        this.hostName = hostName;
    }

    public AuthResponse authenticatePasscode(String username, String passcode) {
        Form form = new Form()
            .param("username", username)
            .param("factor", "passcode")
            .param("passcode", passcode);

        String date = ofPattern("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z").format(now());

        String token = Token.generate(POST, hostName, AUTH_PATH, form, date, key, secret);

        try {
            return call(form, date, token);
        } catch (ProcessingException e) {
            L.error("Problem making request",e);
            return AuthResponse.failure(e.getMessage());
        }
    }

    private AuthResponse call(Form form, String date, String token) {
        Response response = client.target(format("https://%s", hostName)).path(AUTH_PATH )
                .request(APPLICATION_JSON)
                .property(HTTP_AUTHENTICATION_BASIC_USERNAME, key)
                .property(HTTP_AUTHENTICATION_BASIC_PASSWORD, token)
                .header(DATE, date)
                .post(entity(form, APPLICATION_FORM_URLENCODED));
    
        response.bufferEntity();
        L.debug("Received Status: {}", response.getStatus());
        L.debug("Received Payload: {}", response.readEntity(String.class));
        return response.readEntity(AuthResponse.class);
    }
}
