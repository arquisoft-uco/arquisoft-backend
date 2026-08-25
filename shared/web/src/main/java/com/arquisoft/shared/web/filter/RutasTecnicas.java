package com.arquisoft.shared.web.filter;

import java.util.List;

public final class RutasTecnicas {

    private RutasTecnicas() {}

    // Prefijos de las rutas que no pertenecen al dominio: documentacion y sondas de salud.
    // Incluyen el context-path porque los filtros los comparan contra getRequestURI(), que
    // si lo lleva — a diferencia de los requestMatchers de Spring Security, que no.
    //
    // Viven aqui y no en cada filtro porque son tres los que deben coincidir
    // (TrazabilidadFilter, JwtBlacklistFilter, LimitadorSolicitudesFilter): olvidar uno al
    // agregar una ruta tecnica nueva la deja auditada o limitada por cuota sin que nada falle.
    public static final String ACTUATOR = "/api/actuator";
    public static final String SWAGGER_UI = "/api/swagger-ui";
    public static final String API_DOCS = "/api/v3/api-docs";
    public static final String SWAGGER_RESOURCES = "/api/swagger-resources";

    public static final List<String> PREFIJOS =
            List.of(ACTUATOR, SWAGGER_UI, API_DOCS, SWAGGER_RESOURCES);

    // El valor por defecto de un @Value debe ser una expresion constante (JLS 9.7.1),
    // asi que la lista no puede construirse con String.join en tiempo de ejecucion.
    public static final String PREFIJOS_CSV =
            ACTUATOR + "," + SWAGGER_UI + "," + API_DOCS + "," + SWAGGER_RESOURCES;

    public static boolean esRutaTecnica(String uri) {
        return PREFIJOS.stream().anyMatch(uri::startsWith);
    }
}
