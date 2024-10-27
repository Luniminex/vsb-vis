package org.cardvault.core.routing;

import lombok.Getter;

@Getter
public class Response {
    private int status;
    private String body;

    public Response(int status, String body) {
        this.status = status;
        this.body = body;
    }
    public static Response ok(String body) {
        return new Response(200, body);
    }

    public static Response error(int status, String body) {
        return new Response(status, body);
    }
}
