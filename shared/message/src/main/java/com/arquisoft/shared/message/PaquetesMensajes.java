package com.arquisoft.shared.message;

/**
 * Registro de los bundles que componen el catálogo de mensajes.
 *
 * <p>Cada constante es un <em>base name</em> de {@link java.util.ResourceBundle}: la ruta del
 * archivo dentro de {@code src/main/resources} con puntos en lugar de barras y sin la extensión
 * {@code .properties}. Añadir un contexto o un feature nuevo al catálogo consiste en crear el
 * archivo y añadir su base name a {@link #TODOS}.
 *
 * <p>La granularidad es un archivo por contexto, y uno adicional cuando el volumen lo justifica
 * (por ejemplo {@code fichas-api}, que agrupa los textos de documentación OpenAPI y no comparte
 * ciclo de vida con los mensajes de negocio de {@code fichas}).
 */
public final class PaquetesMensajes {

    private PaquetesMensajes() {}

    /** Mensajes transversales: validadores de dominio, errores HTTP genéricos, paginación. */
    public static final String APP = "messages.app";

    /** Contexto fichas: dominio, aplicación e infraestructura. */
    public static final String FICHAS = "messages.fichas";

    /** Contexto fichas: textos de documentación OpenAPI/Swagger. */
    public static final String FICHAS_API = "messages.fichas-api";

    /** Contexto seguridad: autenticación, tokens, sesión, rate limiting. */
    public static final String SEGURIDAD = "messages.seguridad";

    /** Contexto usuarios. */
    public static final String USUARIOS = "messages.usuarios";

    /** Contexto notificaciones: reglas, plantillas de correo y trazas de los consumidores. */
    public static final String NOTIFICACIONES = "messages.notificaciones";

    /**
     * Mensajes de las restricciones Jakarta. Lo lee Hibernate Validator por su cuenta para
     * resolver los {@code message="{clave}"} de las anotaciones; se registra además aquí para que
     * las pruebas puedan afirmar sobre el texto esperado sin volver a escribirlo.
     */
    public static final String VALIDACION = "ValidationMessages";

    /**
     * Orden de resolución de claves. Una clave se busca en cada bundle hasta encontrarla,
     * por lo que el orden solo importa si dos bundles declaran la misma clave — situación
     * que {@code CatalogoMensajesClavesTest} impide.
     */
    public static final String[] TODOS = {
        APP,
        FICHAS,
        FICHAS_API,
        SEGURIDAD,
        USUARIOS,
        NOTIFICACIONES,
        VALIDACION,
    };
}
