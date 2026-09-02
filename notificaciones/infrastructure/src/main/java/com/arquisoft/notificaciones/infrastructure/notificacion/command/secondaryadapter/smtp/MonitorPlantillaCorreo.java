package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.smtp;

import com.arquisoft.notificaciones.infrastructure.config.NotificacionProperties;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.notificaciones.EnvioNotificacionKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "notificacion.plantilla-refresco.habilitado",
        havingValue = "true",
        matchIfMissing = true)
public class MonitorPlantillaCorreo {

    private final RedisFuentePlantillaCorreo fuente;
    private final NotificacionProperties properties;
    private final AppLogger logger;
    private final GestorTraza gestorTraza;

    @Scheduled(fixedDelayString = "${notificacion.plantilla-refresco.intervalo:PT5M}")
    public void refrescar() {
        try (var alcance = gestorTraza.abrir(SolicitudTraza.paraProgramado())) {
            registrarIntento();
        }
    }

    // Captura toda excepcion: Spring cancela para siempre una tarea programada que lanza, y la
    // plantilla quedaria congelada en la version del arranque hasta el proximo despliegue.
    // Una candidata invalida y un Redis caido acaban igual —se conserva la que ya funcionaba—,
    // asi que comparten rama; el motivo va en el log. Va dentro del alcance de traza: en un catch
    // por fuera del try-with-resources el MDC ya esta limpio y el aviso sale sin correlacion.
    private void registrarIntento() {
        try {
            if (fuente.recargar()) {
                logger.info(Mensajes.obtener(EnvioNotificacionKey.LOG_PLANTILLA_ACTUALIZADA),
                        properties.getPlantilla());
            }
        } catch (RuntimeException e) {
            logger.warn(Mensajes.obtener(EnvioNotificacionKey.LOG_PLANTILLA_NO_ACTUALIZADA),
                    properties.getPlantilla(), e.getMessage());
        }
    }
}
