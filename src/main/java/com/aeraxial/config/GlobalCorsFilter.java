package com.aeraxial.config;

import io.quarkus.vertx.http.runtime.filters.Filters;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class GlobalCorsFilter {

    public void setupCors(@Observes Filters filters) {
        filters.register(rc -> {
            HttpServerRequest request = rc.request();
            HttpServerResponse response = rc.response();

            System.out.println("🟢 CORS Filter - " + request.method() + " " + request.path());

            // Agregar headers CORS a TODAS las respuestas
            response.putHeader("Access-Control-Allow-Origin", "*");
            response.putHeader("Access-Control-Allow-Credentials", "true");
            response.putHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
            response.putHeader("Access-Control-Allow-Headers",
                    "Origin, X-Requested-With, Content-Type, Accept, Authorization, X-Client-Id, X-Actor");
            response.putHeader("Access-Control-Max-Age", "3600");

            // Si es OPTIONS (preflight), responder inmediatamente con 200
            if ("OPTIONS".equals(request.method().toString())) {
                System.out.println("✅ CORS Preflight - Responding 200 OK");
                response.setStatusCode(200);
                response.end();
            } else {
                // Para otros métodos, continuar con la cadena
                rc.next();
            }
        }, 10); // Prioridad 10 = ejecutar muy temprano
    }
}