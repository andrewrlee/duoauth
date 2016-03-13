package uk.co.optimisticpanda.duoauth;

import static javax.ws.rs.client.ClientBuilder.newClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleCall {

    private static final Logger L = LoggerFactory.getLogger(SampleCall.class);

    public static void main(String... args) {

        String key = "API-KEY";
        String secret = "SECRET";
        String hostName = "XXXXXXXX.duosecurity.com";

        DuoGateway gateway = new DuoGateway(newClient(), hostName, key, secret);

        AuthResponse response = gateway.authenticatePasscode("jeff", "647660");
        L.info("Successful?: {}", response.isSuccess());
        L.info("Message: {}", response.getMessage().orElse(""));
    }
}
