package com.arquisoft.fichas.infrastructure.web;

public final class FichasRoutes {

    private FichasRoutes() {}

    public static final String FICHAS_PERFIL = "/fichas-perfil";

    public static final String ESTADO_EVALUACION_FICHA = FICHAS_PERFIL + "/estado-evaluacion-ficha";

    public static final String SECURITY_SCHEME = "bearerAuth";
}
