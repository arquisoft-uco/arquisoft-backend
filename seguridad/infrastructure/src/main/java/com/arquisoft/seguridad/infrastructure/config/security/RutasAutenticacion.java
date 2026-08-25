package com.arquisoft.seguridad.infrastructure.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RutasAutenticacion {

    // Las rutas publicas de autenticacion se necesitan en dos formas distintas y esa es la
    // razon de que esta clase exista:
    //
    //   - Spring Security compara sus requestMatchers DESPUES de quitar el context-path,
    //     asi que espera '/auth/login'.
    //   - Un Filter comun compara contra getRequestURI(), que SI lleva el context-path,
    //     asi que espera '/api/auth/login'.
    //
    // Declararlas por separado en SeguridadConfig y en JwtBlacklistFilter dejaba dos copias
    // del mismo dato y solo una leia el yml: cambiar 'rutas.seguridad.auth.login' movia la
    // ruta permitida sin mover la que el filtro se salta, y el fallo era silencioso.
    private final String login;
    private final String refresh;
    private final String validate;

    private final Set<String> publicasConContextPath;

    public RutasAutenticacion(
            @Value("${server.servlet.context-path:}") String contextPath,
            @Value("${rutas.seguridad.auth.base:/auth}") String base,
            @Value("${rutas.seguridad.auth.login:/login}") String login,
            @Value("${rutas.seguridad.auth.refresh:/refresh}") String refresh,
            @Value("${rutas.seguridad.auth.validate:/validate}") String validate) {

        this.login = base + login;
        this.refresh = base + refresh;
        this.validate = base + validate;

        this.publicasConContextPath = Set.of(
                contextPath + this.login,
                contextPath + this.refresh,
                contextPath + this.validate);
    }

    public String login() {
        return login;
    }

    public String refresh() {
        return refresh;
    }

    public String validate() {
        return validate;
    }

    public boolean esPublica(String uri) {
        return publicasConContextPath.contains(uri);
    }
}
