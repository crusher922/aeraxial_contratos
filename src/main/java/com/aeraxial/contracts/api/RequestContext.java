package com.aeraxial.contracts.api;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class RequestContext {
    private RequestContext(){}
    public static long requireClientId(String clientIdHeader) {
        if (clientIdHeader == null || clientIdHeader.isBlank()) {
            throw new WebApplicationException("Missing X-Client-Id header", Response.Status.BAD_REQUEST);
        }
        try {
            return Long.parseLong(clientIdHeader.trim());
        } catch (NumberFormatException e) {
            throw new WebApplicationException("Invalid X-Client-Id header", Response.Status.BAD_REQUEST);
        }
    }

    public static String actorOrSystem(String actorHeader) {
        return (actorHeader == null || actorHeader.isBlank()) ? "system" : actorHeader.trim();
    }
}
