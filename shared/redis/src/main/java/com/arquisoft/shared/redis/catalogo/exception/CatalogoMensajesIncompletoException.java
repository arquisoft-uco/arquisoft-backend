package com.arquisoft.shared.redis.catalogo.exception;

import com.arquisoft.shared.exception.InfrastructureException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Aborta el arranque cuando el catálogo de Redis no está completo.
 *
 * <p>Es el fail-fast de ADR-013 v1.1: si falta una sola de las claves que el código declara, la
 * aplicación no levanta. Sin él, una clave ausente dejaría de degradar un mensaje para romperlo en
 * producción sin que nadie se entere, así que el fallo se adelanta al despliegue.
 *
 * <p>El mensaje nombra las claves que faltan agrupadas por contexto, que es lo que quien despliega
 * necesita para saber qué archivo del catálogo versionado no se cargó.
 */
public final class CatalogoMensajesIncompletoException extends InfrastructureException {

    private static final String CODIGO = "CATALOGO_MENSAJES_INCOMPLETO";
    private static final String CODIGO_SIN_CONEXION = "CATALOGO_MENSAJES_SIN_CONEXION";
    private static final String CODIGO_ARIDAD = "CATALOGO_MENSAJES_ARIDAD_INCONSISTENTE";

    private static final int MAXIMO_A_NOMBRAR = 20;
    private static final String SEPARADOR_CONTEXTO = ".";
    private static final String SIN_CONTEXTO = "(sin contexto)";
    private static final String SEPARADOR_CLAVES = ", ";
    private static final String SEPARADOR_GRUPOS = "; ";

    private static final String FORMATO_FALTANTES =
            "El catálogo de mensajes en Redis está incompleto: faltan %d de %d claves declaradas. Detalle por contexto: %s";
    private static final String FORMATO_GRUPO = "%s -> %s";
    private static final String FORMATO_RECORTE = "%s, ... (%d más)";
    private static final String FORMATO_ARIDAD =
            "El catálogo de mensajes en Redis tiene %d patrones cuyo número de parámetros no coincide "
                    + "con el que declara su clave. Detalle por contexto: %s";
    private static final String FORMATO_SIN_CONEXION =
            "No hay conexión con Redis para cargar el catálogo de mensajes. La aplicación no puede arrancar sin catálogo.";

    private CatalogoMensajesIncompletoException(String mensaje, String codigo) {
        super(mensaje, codigo);
    }

    private CatalogoMensajesIncompletoException(String mensaje, String codigo, Throwable causa) {
        super(mensaje, codigo, causa);
    }

    /**
     * Construye la excepción de catálogo incompleto.
     *
     * @param faltantes  claves declaradas que Redis no resolvió
     * @param declaradas total de claves declaradas
     * @return la excepción, con las claves faltantes agrupadas por contexto
     */
    public static CatalogoMensajesIncompletoException porClavesFaltantes(List<String> faltantes, int declaradas) {
        return new CatalogoMensajesIncompletoException(
                FORMATO_FALTANTES.formatted(faltantes.size(), declaradas, describirPorContexto(faltantes)), CODIGO);
    }

    /**
     * Construye la excepción de ausencia de conexión.
     *
     * @param causa fallo original del cliente de Redis
     * @return la excepción
     */
    public static CatalogoMensajesIncompletoException porFaltaDeConexion(Throwable causa) {
        return new CatalogoMensajesIncompletoException(FORMATO_SIN_CONEXION, CODIGO_SIN_CONEXION, causa);
    }

    /**
     * Construye la excepción de aridad inconsistente.
     *
     * @param desajustes claves cuyo patrón no admite los parámetros que declaran
     * @return la excepción, con las claves agrupadas por contexto
     */
    public static CatalogoMensajesIncompletoException porAridadInconsistente(List<String> desajustes) {
        return new CatalogoMensajesIncompletoException(
                FORMATO_ARIDAD.formatted(desajustes.size(), describirPorContexto(desajustes)), CODIGO_ARIDAD);
    }

    private static String describirPorContexto(List<String> faltantes) {
        Map<String, List<String>> porContexto = faltantes.stream()
                .collect(Collectors.groupingBy(CatalogoMensajesIncompletoException::contextoDe));

        return porContexto.entrySet().stream()
                .map(entrada -> FORMATO_GRUPO.formatted(entrada.getKey(), recortar(entrada.getValue())))
                .collect(Collectors.joining(SEPARADOR_GRUPOS));
    }

    private static String contextoDe(String clave) {
        int corte = clave.indexOf(SEPARADOR_CONTEXTO);
        return corte > 0 ? clave.substring(0, corte) : SIN_CONTEXTO;
    }

    private static String recortar(List<String> claves) {
        if (claves.size() <= MAXIMO_A_NOMBRAR) {
            return String.join(SEPARADOR_CLAVES, claves);
        }

        return FORMATO_RECORTE.formatted(
                String.join(SEPARADOR_CLAVES, claves.subList(0, MAXIMO_A_NOMBRAR)), claves.size() - MAXIMO_A_NOMBRAR);
    }
}
