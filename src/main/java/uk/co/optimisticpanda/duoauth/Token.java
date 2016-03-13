package uk.co.optimisticpanda.duoauth;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.glassfish.jersey.uri.UriComponent.encode;
import static org.glassfish.jersey.uri.UriComponent.Type.QUERY_PARAM_SPACE_ENCODED;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map.Entry;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.Form;

import jersey.repackaged.com.google.common.base.Throwables;

public class Token {

    public static String generate(String method, String hostName, String path, 
            Form form, String date, String key, String secret) {

        String params = form.asMap().entrySet().stream()
                .sorted(Entry.comparingByKey())
                .map(Token::asParams)
                .collect(joining("&"));

        String raw = join("\n", asList(
                date, method.toUpperCase(), hostName.toLowerCase(), path, params));
        return toHex(hmacSha1(secret.getBytes(), raw.getBytes()));
    }

    private static String asParams(Entry<String, List<String>> entry) {
        return format("%s=%s", 
                encode(entry.getKey(), QUERY_PARAM_SPACE_ENCODED), 
                encode(entry.getValue().get(0), QUERY_PARAM_SPACE_ENCODED));
    }

    private static String toHex(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    private static byte[] hmacSha1(byte[] key_bytes, byte[] text_bytes) {
        try {
            Mac hmacSha1 = Mac.getInstance("HmacSHA1");
            SecretKeySpec macKey = new SecretKeySpec(key_bytes, "RAW");
            hmacSha1.init(macKey);
            return hmacSha1.doFinal(text_bytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw Throwables.propagate(e);
        }
    }
}
