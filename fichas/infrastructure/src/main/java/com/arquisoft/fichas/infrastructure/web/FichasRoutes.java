package com.arquisoft.fichas.infrastructure.web;

/**
 * Rutas base y constantes de configuración web del contexto fichas.
 *
 * <p>Evita repetir el path del recurso y el nombre del esquema de seguridad
 * en cada adaptador: renombrar el recurso se hace en un solo lugar.</p>
 */
public final class FichasRoutes {

    private FichasRoutes() {}

    /** Ruta base del recurso fichas de perfil. */
    public static final String FICHAS_PERFIL = "/fichas-perfil";

    /** Sub-ruta del recurso de estados de evaluación de ficha. */
    public static final String ESTADO_EVALUACION_FICHA = FICHAS_PERFIL + "/estado-evaluacion-ficha";

    /** Nombre del esquema de seguridad declarado en la configuración OpenAPI. */
    public static final String SECURITY_SCHEME = "bearerAuth";
}
