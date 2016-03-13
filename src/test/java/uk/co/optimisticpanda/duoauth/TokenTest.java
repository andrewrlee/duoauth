package uk.co.optimisticpanda.duoauth;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Base64;

import javax.ws.rs.core.Form;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.optimisticpanda.duoauth.Token;

public class TokenTest {

    private static final Logger L = LoggerFactory.getLogger(TokenTest.class);

    // This checks that the token we generate matches the example from here: https://duo.com/docs/authapi#authentication
    @Test
    public void generateSameTokenAsExample() {
        Form form = new Form("username", "root")
            .param("realname", "First Last");
        String apiKey = "DIWJ8X6AEYOR5OMC6TQ1";
        String generated = Token.generate(
                "POST", 
                "api-XXXXXXXX.duosecurity.com", 
                "/accounts/v1/account/list", 
                form, 
                "Tue, 21 Aug 2012 17:29:18 -0000", 
                apiKey,
                "Zh5eGmUq9zpfQnyUIu5OL9iWoMMv5ZNmk3zLJ4Ep");

        String expected = "RElXSjhYNkFFWU9SNU9NQzZUUTE6MmQ5N2Q2MTY2MzE5NzgxYjVhM2EwN2FmMzlkMzY2ZjQ5MTIzNGVkYw==";
        String generatedTokenValue = Base64.getEncoder().encodeToString((apiKey + ":" + generated).getBytes());
        L.info("Generated      : " + generatedTokenValue);
        L.info("Expected       : " + expected);
        assertThat(generatedTokenValue).isEqualTo(expected);
    }
}
