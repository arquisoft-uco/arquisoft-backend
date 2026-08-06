package com.arquisoft.shared.message;

/**
 * Contrato de una clave del catálogo.
 *
 * <p>Existe para que {@link MessageCatalog} deje de aceptar {@code String}. Con la firma antigua
 * cualquier literal compilaba — una clave mal escrita, o la de otro contexto — y el fallo solo
 * aparecía en ejecución como el marcador {@code ??clave??}. Al tipar el puerto sobre esta interfaz,
 * las únicas claves que el compilador admite son las declaradas en los enums de {@code key.*}.
 *
 * <p>La implementan enums, uno por feature (por ejemplo {@code key.fichas.FichaPerfilKey}), lo que
 * además da a {@code MessageCatalogClavesTest} un {@code values()} exhaustivo en lugar de un
 * recorrido por reflexión que puede dejarse campos fuera en silencio.
 *
 * <p>Quedan fuera de este contrato las claves que se consumen dentro de una anotación
 * ({@code annotation.ValidationKeys}, {@code annotation.FichasApiKeys}): el valor de un atributo de
 * anotación debe ser una expresión constante (JLS §9.7.1) y una llamada a {@link #clave()} no lo es.
 * Esas siguen siendo {@code String} por obligación del lenguaje, y no las resuelve este catálogo
 * sino Hibernate Validator y springdoc.
 */
public interface MessageKey {

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
     * hasta acertar. Ver {@link MessageBundles}.
     *
     * @return el base name del bundle propietario
     */
    String bundle();
}
