package com.arquisoft.shared.message;

/**
 * Contrato de una clave del catálogo.
 *
 * <p>Existe para que {@link CatalogoMensajes} deje de aceptar {@code String}. Con la firma antigua
 * cualquier literal compilaba — una clave mal escrita, o la de otro contexto — y el fallo solo
 * aparecía en ejecución como el marcador {@code ??clave??}. Al tipar el puerto sobre esta interfaz,
 * las únicas claves que el compilador admite son las declaradas en los enums de {@code key.*}.
 *
 * <p>La implementan enums, uno por feature (por ejemplo {@code key.fichas.FichaPerfilKey}), lo que
 * además da a {@code CatalogoMensajesClavesTest} un {@code values()} exhaustivo en lugar de un
 * recorrido por reflexión que puede dejarse campos fuera en silencio.
 *
 * <p>Quedan fuera de este contrato las claves que se consumen dentro de una anotación
 * ({@code annotation.FichasApiKeys}): el valor de un atributo de anotación debe ser una expresión
 * constante (JLS §9.7.1) y una llamada a {@link #clave()} no lo es. Esas siguen siendo
 * {@code String} por obligación del lenguaje, y no las resuelve este catálogo sino springdoc.
 * La validación de formato de un identificador (por ejemplo un UUID) nunca vive en una anotación
 * Jakarta — ni propia ni de librería — precisamente para no necesitar esta segunda vía: se valida
 * en el {@code Command} y su texto sale de este catálogo como cualquier otro.
 */
public interface ClaveMensaje {

    /**
     * Clave completa, con el esquema {@code contexto.capa.objeto.tipo.descripcion}.
     *
     * @return la clave tal como aparece en el archivo {@code .properties}
     */
    String clave();

    /**
     * Base name del bundle que declara esta clave.
     *
     * <p>Permite resolverla contra su archivo directamente, en lugar de recorrer todos los bundles
     * hasta acertar. Ver {@link PaquetesMensajes}.
     *
     * @return el base name del bundle propietario
     */
    String paquete();
}
