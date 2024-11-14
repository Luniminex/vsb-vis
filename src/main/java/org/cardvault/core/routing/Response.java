package org.cardvault.core.routing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.cardvault.core.utils.DefaultObjectMapper;

@Getter
public class Response {
    private final int status;
    private final String body;

    private final ObjectMapper objectMapper = DefaultObjectMapper.createObjectMapper();

    public Response(int status, String body) {
        this.status = status;
        this.body = body;
    }
    public static Response ok(Object object) {
        try {
            return new Response(200, convertObjectToJson(object));
        } catch (JsonProcessingException e) {
            return error(500, "Internal server mapping error " + e.getMessage());
        }
    }

    public static Response error(int status, Object object) {
        try {
            return new Response(status, convertObjectToJson(object));
        } catch (JsonProcessingException e) {
            return new Response(status, "Internal server error " + e.getMessage());
        }
    }

    private static String convertObjectToJson(Object object) throws JsonProcessingException {
        return DefaultObjectMapper.createObjectMapper().writeValueAsString(object);
    }
}
