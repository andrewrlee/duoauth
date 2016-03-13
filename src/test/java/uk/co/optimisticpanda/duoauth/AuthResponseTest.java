package uk.co.optimisticpanda.duoauth;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthResponseTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    
    @Test
    public void parseFail() throws IOException {
        AuthResponse response = mapper.readValue("{"
                + "\"response\": {"
                    + "\"reason\": \"Invalid passcode\","
                    + "\"result\": \"deny\","
                    + "\"status\": \"deny\","
                    + "\"status_msg\": \"This passcode has already been used. Please generate a new passcode and try again.\""
                 + "},"
                + "\"stat\": \"OK\""
                + "}", AuthResponse.class);
        
        assertThat(response.isAllowed()).isFalse();
        assertThat(response.isError()).isFalse();
        assertThat(response.getCode()).isEmpty();
        assertThat(response.getReason()).contains("Invalid passcode");
        assertThat(response.getMessage()).contains("This passcode has already been used. Please generate a new passcode and try again.");
    }

    @Test
    public void parseSuccess() throws IOException {
        AuthResponse response = mapper.readValue("{"
                + "\"response\": {"
                    + "\"reason\": \"Valid passcode\","
                    + "\"result\": \"allow\","
                    + "\"status\": \"allow\","
                    + "\"status_msg\": \"Logging you in...\""
                 + "},"
                + "\"stat\": \"OK\""
                + "}", AuthResponse.class);
        
        assertThat(response.isAllowed()).isTrue();
        assertThat(response.isError()).isFalse();
        assertThat(response.getCode()).isEmpty();
        assertThat(response.getReason()).contains("Valid passcode");
        assertThat(response.getMessage()).contains("Logging you in...");
    }
    
    @Test
    public void parseError() throws IOException {
        AuthResponse response = mapper.readValue("{"
                + "\"code\": 40102,"
                + "\"message\": \"Invalid integration key in request credentials\","
                + "\"stat\": \"FAIL\""
                + "}", AuthResponse.class);
        
        assertThat(response.isAllowed()).isFalse();
        assertThat(response.isError()).isTrue();
        assertThat(response.getCode()).contains("40102");
        assertThat(response.getReason()).isEmpty();
        assertThat(response.getMessage()).contains("Invalid integration key in request credentials");
    }
}
