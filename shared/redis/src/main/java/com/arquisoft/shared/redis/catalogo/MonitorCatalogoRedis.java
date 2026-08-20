package com.arquisoft.shared.redis.catalogo;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Devuelve el catálogo al estado sano cuando Redis vuelve.
 *
 * <p>Solo actúa si el adaptador está degradado: en estado sano no toca Redis, porque cada
 * resolución ya lo consulta. Al recuperar la conexión recarga el catálogo completo, y solo vuelve
 * a sano si la recarga quedó completa — una recarga parcial no puede pasar por recuperación.
 */
public class MonitorCatalogoRedis {

    // Igual que en el adaptador, estos textos no salen del catálogo: describen que el catálogo
    // mismo está caído, así que resolverlos por él sería circular.
    private static final String LOG_RECUPERADO =
            "Catálogo de mensajes recuperado: Redis responde de nuevo. Claves recargadas: {}";
    private static final String LOG_RECARGA_INCOMPLETA =
            "Redis responde pero el catálogo sigue incompleto, se mantiene el estado degradado. Faltan {} de {} claves";
    private static final String LOG_SIGUE_CAIDO =
            "El catálogo de mensajes sigue degradado: Redis no responde al reintento";

    private final CatalogoMensajesRedis catalogo;
    private final AppLogger logger;
    private final GestorTraza gestorTraza;

    public MonitorCatalogoRedis(CatalogoMensajesRedis catalogo, AppLogger logger, GestorTraza gestorTraza) {
        this.catalogo = catalogo;
        this.logger = logger;
        this.gestorTraza = gestorTraza;
    }

    /**
     * Reintenta la conexión y recarga el catálogo si Redis ha vuelto.
     *
     * <p>Captura toda excepción: Spring cancela para siempre una tarea programada que lanza, y eso
     * dejaría la aplicación degradada de forma permanente aunque Redis se recuperase después.
     */
    @Scheduled(fixedDelayString = "${arquisoft.catalogo.reintento-intervalo:PT30S}")
    public void reintentar() {
        try (var alcance = gestorTraza.abrir(SolicitudTraza.paraProgramado())) {
            if (!catalogo.estaDegradado()) {
                return;
            }

            if (!catalogo.hayConexion()) {
                logger.warn(LOG_SIGUE_CAIDO);
                return;
            }

            ResultadoCarga resultado = catalogo.recargar();
            if (!resultado.esCompleto()) {
                logger.error(LOG_RECARGA_INCOMPLETA, resultado.faltantes().size(), resultado.declaradas());
                return;
            }

            catalogo.marcarSano();
            logger.info(LOG_RECUPERADO, resultado.cargadas());
        } catch (RuntimeException e) {
            logger.error(LOG_SIGUE_CAIDO, e);
        }
    }
}
