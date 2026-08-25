package com.arquisoft.shared.message;

import java.util.List;

/**
 * Contextos que agrupan las claves del catálogo.
 *
 * <p>Es el primer segmento de la clave, y también el nombre del archivo del catálogo versionado que
 * carga {@code catalogo/cargar.sh}. No es un base name que se resuelva archivo por archivo: Redis
 * es un espacio de claves plano, así que el contexto solo agrupa los datos de carga y sirve para
 * validar prefijos en los tests.
 */
public final class ContextosCatalogo {

    private ContextosCatalogo() {}

    /** Mensajes transversales: validadores, errores HTTP genéricos, paginación, consulta dinámica. */
    public static final String APP = "app";

    /** Contexto fichas. */
    public static final String FICHAS = "fichas";

    /** Contexto seguridad. */
    public static final String SEGURIDAD = "seguridad";

    /** Contexto usuarios. */
    public static final String USUARIOS = "usuarios";

    /** Contexto notificaciones. */
    public static final String NOTIFICACIONES = "notificaciones";

    /** Todos los contextos, en el orden en que se cargan. */
    public static final List<String> TODOS = List.of(APP, FICHAS, SEGURIDAD, USUARIOS, NOTIFICACIONES);
}
