package com.arquisoft.seguridad.infrastructure.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;

@Component
public class RutasPublicas {

    private static final String PROVEEDOR_LOGIN_DIRECTO = "keycloak";

    // Spring Security compara sus requestMatchers DESPUES de quitar el context-path ('/auth/login'),
    // mientras que un Filter comun compara contra getRequestURI(), que SI lo lleva
    // ('/api/auth/login'). Una sola fuente evita que las dos copias diverjan en silencio.
    private final AntPathMatcher comparador = new AntPathMatcher();
    private final String contextPath;
    private final List<String> publicas;
    private final List<String> autenticadas;

    public RutasPublicas(
            @Value("${server.servlet.context-path:}") String contextPath,
            @Value("${security.authorization.public-paths:}") List<String> publicPaths,
            @Value("${security.authorization.authenticated-paths:}") List<String> authenticatedPaths,
            @Value("${security.direct-login.provider:keycloak}") String proveedorLoginDirecto,
            Environment entorno) {
        this.contextPath = contextPath;
        var rutasPublicas = new ArrayList<>(publicPaths);
        if (PROVEEDOR_LOGIN_DIRECTO.equalsIgnoreCase(proveedorLoginDirecto)) {
            var base = entorno.getProperty("rutas.seguridad.auth.base", "/auth");
            rutasPublicas.add(base + entorno.getProperty("rutas.seguridad.auth.login", "/login"));
            rutasPublicas.add(base + entorno.getProperty("rutas.seguridad.auth.refresh", "/refresh"));
            rutasPublicas.add(base + entorno.getProperty("rutas.seguridad.auth.validate", "/validate"));
        }
        this.publicas = rutasPublicas.stream().filter(ruta -> !ruta.isBlank()).toList();
        this.autenticadas = authenticatedPaths.stream().filter(ruta -> !ruta.isBlank()).toList();
    }

    public String[] patronesPublicos() {
        return publicas.toArray(String[]::new);
    }

    public boolean esPublica(String uri) {
        return coincide(publicas, uri);
    }

    public boolean esExentaDePep(String uri) {
        return coincide(autenticadas, uri);
    }

    private boolean coincide(List<String> patrones, String uri) {
        return patrones.stream().anyMatch(patron ->
                comparador.match(patron, uri) || comparador.match(contextPath + patron, uri));
    }
}
