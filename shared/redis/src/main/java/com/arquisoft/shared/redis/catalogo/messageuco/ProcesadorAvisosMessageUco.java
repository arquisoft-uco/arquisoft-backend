package com.arquisoft.shared.redis.catalogo.messageuco;

import co.edu.uco.messageucolab.client.api.exception.MessageClientResponseException;
import co.edu.uco.messageucolab.client.api.exception.MessageClientTransportException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.redis.catalogo.exception.AvisoMessageUcoInvalidoException;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 * Decide qué hace el catálogo con los avisos de cambio de MessageUcoLab (ADR-017).
 *
 * <p>Los avisos se aplican por lotes: la escucha junta los que llegan en una ventana corta. El
 * aviso no trae el texto, solo el tipo y el código, así que cada código a actualizar es una
 * consulta a MessageUcoLab. Una carga masiva publica un aviso por mensaje, y miles de consultas
 * seguidas chocan con el límite de solicitudes del gateway; por eso un lote grande se resuelve con
 * una sola consulta de todos los mensajes.
 *
 * <ul>
 *   <li>{@code MessageCreated} y {@code MessageUpdated}: se trae ese mensaje y se escribe su clave.</li>
 *   <li>{@code MessagesChanged}, o más de {@code umbralRecargaCompleta} códigos en el lote: se
 *       sincroniza el catálogo completo, una vez por lote.</li>
 *   <li>{@code MessageDeleted}: no se borra nada. Una clave faltante impide el siguiente arranque, y
 *       el código del mensaje borrado ya no permite saber qué clave era.</li>
 * </ul>
 *
 * <p>Aplicar dos veces el mismo aviso deja Redis igual (el texto se pide al momento), así que no se
 * descartan repetidos. Un fallo que vale la pena reintentar se propaga, para que la escucha pida
 * otra entrega del lote; uno que no, se registra y se sigue con el resto.
 */
public class ProcesadorAvisosMessageUco {

    static final String CREADO = "MessageCreated";
    static final String MODIFICADO = "MessageUpdated";
    static final String ELIMINADO = "MessageDeleted";
    static final String CAMBIO_MASIVO = "MessagesChanged";

    private static final String CAMPO_TIPO = "eventType";
    private static final String CAMPO_CODIGO = "messageCode";
    private static final String CAMPO_ID = "eventId";

    // Textos literales por la misma razón que en el sincronizador: describen al proveedor del catálogo.
    private static final String LOG_RECIBIDO = "Aviso de MessageUcoLab recibido: {} {} (evento {})";
    private static final String LOG_SIN_CODIGO = "Aviso {} de MessageUcoLab sin código de mensaje; se ignora (evento {})";
    private static final String LOG_ELIMINADO =
            "MessageUcoLab eliminó el mensaje {}; Redis conserva su último texto (evento {})";
    private static final String LOG_TIPO_DESCONOCIDO = "Aviso de MessageUcoLab de tipo desconocido '{}'; se ignora (evento {})";
    private static final String LOG_RECARGA_COMPLETA =
            "Lote de {} aviso(s) de MessageUcoLab con {} mensaje(s) a actualizar: se recarga el catálogo completo";
    private static final String LOG_CODIGO_DESCARTADO =
            "No se puede aplicar el aviso del mensaje {} de MessageUcoLab, se descarta: {}";
    private static final String MENSAJE_INVALIDO = "Aviso de MessageUcoLab con JSON inválido";

    private static final int NO_AUTORIZADO = 401;
    private static final int DEMASIADAS_SOLICITUDES = 429;
    private static final int ERROR_SERVIDOR = 500;

    private final SincronizadorCatalogoMessageUco sincronizador;
    private final JsonMapper lectorJson;
    private final int umbralRecargaCompleta;
    private final AppLogger logger;

    public ProcesadorAvisosMessageUco(
            SincronizadorCatalogoMessageUco sincronizador,
            JsonMapper lectorJson,
            int umbralRecargaCompleta,
            AppLogger logger) {
        this.sincronizador = sincronizador;
        this.lectorJson = lectorJson;
        this.umbralRecargaCompleta = umbralRecargaCompleta;
        this.logger = logger;
    }

    /**
     * Lee un aviso tal como llega de Pulsar.
     *
     * @param carga el JSON del aviso
     * @return el aviso
     * @throws AvisoMessageUcoInvalidoException si la carga no es JSON
     */
    public AvisoMessageUco leer(String carga) {
        JsonNode aviso;
        try {
            aviso = lectorJson.readTree(UtilObjeto.aplicarPorDefecto(carga, UtilTexto.VACIO));
        } catch (JacksonException e) {
            throw new AvisoMessageUcoInvalidoException(MENSAJE_INVALIDO, e);
        }
        var leido = new AvisoMessageUco(
                aviso.path(CAMPO_TIPO).asString(UtilTexto.VACIO),
                aviso.path(CAMPO_CODIGO).asString(UtilTexto.VACIO),
                aviso.path(CAMPO_ID).asString(UtilTexto.VACIO));
        logger.debug(LOG_RECIBIDO, leido.tipo(), leido.codigo(), leido.evento());
        return leido;
    }

    /**
     * Aplica un lote de avisos al catálogo.
     *
     * @param avisos los avisos del lote, en el orden en que llegaron
     */
    public void aplicar(List<AvisoMessageUco> avisos) {
        boolean cambioMasivo = false;
        Set<String> codigos = new LinkedHashSet<>();

        for (AvisoMessageUco aviso : avisos) {
            switch (aviso.tipo()) {
                case CREADO, MODIFICADO -> {
                    if (UtilTexto.esVacioONulo(aviso.codigo())) {
                        logger.warn(LOG_SIN_CODIGO, aviso.tipo(), aviso.evento());
                    } else {
                        codigos.add(aviso.codigo());
                    }
                }
                case CAMBIO_MASIVO -> cambioMasivo = true;
                case ELIMINADO -> logger.info(LOG_ELIMINADO, aviso.codigo(), aviso.evento());
                default -> logger.warn(LOG_TIPO_DESCONOCIDO, aviso.tipo(), aviso.evento());
            }
        }

        if (cambioMasivo || codigos.size() > umbralRecargaCompleta) {
            logger.info(LOG_RECARGA_COMPLETA, avisos.size(), codigos.size());
            sincronizador.sincronizar();
            return;
        }

        for (String codigo : codigos) {
            try {
                sincronizador.actualizar(codigo);
            } catch (RuntimeException e) {
                if (esTransitorio(e)) {
                    throw e;
                }
                logger.error(LOG_CODIGO_DESCARTADO, codigo, e.getMessage());
            }
        }
    }

    /**
     * Si vale la pena volver a entregar un lote que falló con este error: MessageUcoLab caído o
     * lento, el token rechazado (caducó o el proveedor de identidad no respondía) o demasiadas
     * solicitudes. El resto —un 404 porque el mensaje ya no existe, un JSON inválido— fallaría
     * igual en cada reintento.
     *
     * @param error lo que lanzó {@link #leer(String)} o {@link #aplicar(List)}
     * @return {@code true} si se debe reintentar
     */
    public static boolean esTransitorio(Throwable error) {
        for (Throwable actual = error; actual != null; actual = actual.getCause()) {
            if (actual instanceof MessageClientTransportException || actual instanceof TimeoutException) {
                return true;
            }
            if (actual instanceof MessageClientResponseException respuesta) {
                int estado = respuesta.getStatusCode();
                return estado >= ERROR_SERVIDOR || estado == NO_AUTORIZADO || estado == DEMASIADAS_SOLICITUDES;
            }
            if (actual instanceof AvisoMessageUcoInvalidoException) {
                return false;
            }
        }
        // Lo no reconocido —Redis caído, un fallo de red sin envolver— se reintenta: descartarlo
        // dejaría el texto viejo hasta el próximo arranque.
        return true;
    }

    /**
     * Un aviso de cambio.
     *
     * @param tipo   {@code MessageCreated}, {@code MessageUpdated}, {@code MessageDeleted} o {@code MessagesChanged}
     * @param codigo código del mensaje ({@code MSG_...}); vacío en {@code MessagesChanged}
     * @param evento identificador del aviso, solo para los logs
     */
    public record AvisoMessageUco(String tipo, String codigo, String evento) {
    }
}
