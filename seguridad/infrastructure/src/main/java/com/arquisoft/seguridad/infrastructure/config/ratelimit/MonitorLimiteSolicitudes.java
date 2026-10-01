package com.arquisoft.seguridad.infrastructure.config.ratelimit;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.LimiteSolicitudesKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Devuelve el limitador a la cuota distribuida cuando Redis vuelve.
 *
 * <p>Sin esta pieza el fallback sería una trampa: una sola desconexión pasaría el limitador a cuota
 * por instancia para siempre, y con varias réplicas el límite efectivo quedaría multiplicado por su
 * número hasta el próximo despliegue, sin que nada lo delatara.
 */
@Component
@RequiredArgsConstructor
public class MonitorLimiteSolicitudes {

    private final RedisBucketResolver resolver;
    private final AppLogger logger;
    private final GestorTraza gestorTraza;

    /**
     * Reintenta la conexión y restablece la cuota distribuida si Redis ha vuelto.
     *
     * <p>Captura toda excepción: Spring cancela para siempre una tarea programada que lanza, y eso
     * dejaría el limitador degradado de forma permanente aunque Redis se recuperase después.
     *
     * <p>Abre su propio alcance de traza porque un método {@code @Scheduled} no hereda ninguno: sin
     * él, sus líneas de log saldrían sin ningún campo de correlación.
     */
    @Scheduled(fixedDelayString = "${arquisoft.limite-solicitudes.reintento-intervalo:PT30S}")
    public void reintentar() {
        try (var alcance = gestorTraza.abrir(SolicitudTraza.paraProgramado())) {
            if (!resolver.estaDegradado()) {
                return;
            }

            if (!resolver.hayConexion()) {
                logger.warn(LimiteSolicitudesKey.LOG_SIGUE_DEGRADADO);
                return;
            }

            int locales = resolver.ipsConCuotaLocal();
            resolver.marcarSano();
            logger.info(LimiteSolicitudesKey.LOG_RECUPERADO, locales);
        } catch (RuntimeException e) {
            logger.error(LimiteSolicitudesKey.LOG_SIGUE_DEGRADADO, e);
        }
    }
}
