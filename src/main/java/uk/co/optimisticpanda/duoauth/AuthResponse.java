package uk.co.optimisticpanda.duoauth;

import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthResponse {

    @JsonProperty
    private Response response;
    @JsonProperty
    private String stat;
    @JsonProperty
    private String code;
    @JsonProperty
    private String message;

    @JsonCreator
    private AuthResponse() {
    }

    public static AuthResponse failure(String message) {
        AuthResponse response = new AuthResponse();
        response.stat = "FAIL";
        response.message = message;
        response.code = "unspecified";
        return response;
    }
    
    public boolean isAllowed() {
        return response != null && response.status.equals("allow");
    }

    public boolean isError() {
        return !stat.equals("OK");
    }

    
    public Optional<String> getMessage() {
        Optional<String> mes = Optional.ofNullable(response).flatMap(resp -> Optional.ofNullable(resp.statusMessage));
        return mes.isPresent() ? mes : Optional.ofNullable(message);
    }

    public Optional<String> getReason() {
        return Optional.ofNullable(response).flatMap(resp -> Optional.ofNullable(resp.reason));
    }

    public Optional<String> getCode() {
        return Optional.ofNullable(code);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(response, stat);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AuthResponse other = (AuthResponse) obj;
        return Objects.equals(response, other.response) 
                && Objects.equals(stat, other.stat);
    }

    private static class Response {
        @JsonProperty
        private String reason;
        @JsonProperty
        private String result;
        @JsonProperty
        private String status;
        @JsonProperty("status_msg")
        private String statusMessage;

        @Override
        public int hashCode() {
            return Objects.hash(reason, result, status, statusMessage);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Response other = (Response) obj;
            return Objects.equals(reason, other.reason) 
                    && Objects.equals(result, other.result) 
                    && Objects.equals(status, other.status) 
                    && Objects.equals(statusMessage, other.statusMessage);
        }
    }
}
