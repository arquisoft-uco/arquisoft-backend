package com.arquisoft.shared.message;

import java.util.regex.Pattern;

/**
 * Categoría de un mensaje, deducida del cuarto segmento de su clave.
 *
 * <p>El esquema {@code contexto.capa.objeto.tipo.descripcion} ya codifica de qué tipo de texto se
 * trata, así que la categoría se parsea de la clave en lugar de declararse en cada una de las 299
 * constantes de {@code key.*}. Sirve para elegir el texto de respaldo cuando el catálogo no puede
 * resolver la clave — ver {@link CatalogoMensajesRespaldo}.
 */
public enum CategoriaMensaje {

    /** Mensajes de error de negocio o de infraestructura, visibles para el cliente. */
    ERROR,

    /** Mensajes de los validadores de integridad de datos. */
    VALIDACION,

    /** Patrones de log, con marcadores {@code {}} de SLF4J. Audiencia técnica. */
    LOG,

    /** Textos de documentación OpenAPI. No se resuelven en tiempo de ejecución. */
    API;

    /** Categoría asignada a una clave cuyo cuarto segmento no se reconoce. */
    public static final CategoriaMensaje POR_DEFECTO = ERROR;

    private static final int SEGMENTO_TIPO = 3;
    private static final String SEPARADOR = Pattern.quote(".");

    /**
     * Deduce la categoría a partir del cuarto segmento de la clave.
     *
     * @param clave clave completa, con el esquema {@code contexto.capa.objeto.tipo.descripcion}
     * @return la categoría correspondiente, o {@link #POR_DEFECTO} si no se reconoce
     */
    public static CategoriaMensaje desde(String clave) {
        if (clave == null) {
            return POR_DEFECTO;
        }

        String[] segmentos = clave.split(SEPARADOR);
        if (segmentos.length <= SEGMENTO_TIPO) {
            return POR_DEFECTO;
        }

        for (CategoriaMensaje categoria : values()) {
            if (categoria.name().equalsIgnoreCase(segmentos[SEGMENTO_TIPO])) {
                return categoria;
            }
        }

        return POR_DEFECTO;
    }
}
