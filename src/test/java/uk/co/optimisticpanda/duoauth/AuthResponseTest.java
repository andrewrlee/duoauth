package uk.co.optimisticpanda.duoauth;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import uk.co.optimisticpanda.duoauth.AuthResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthResponseTest {

    private static ObjectMapper mapper = new ObjectMapper();
    
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
        
        assertThat(response.isSuccess()).isFalse();
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
        
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getReason()).contains("Valid passcode");
        assertThat(response.getMessage()).contains("Logging you in...");
    }
}
