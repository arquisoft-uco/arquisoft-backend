package com.arquisoft.notificaciones.infrastructure.config;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.ReintentarNotificacionesFallidasInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.ReintentarNotificacionesFallidasCommand;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

// El reintento no puede vivir dentro del consumidor: con prefetch=1 un SMTP caido dejaria el
// listener bloqueado, y la reentrega por la cola tampoco sirve porque la idempotencia por
// event_id devolveria Duplicada sin reenviar nada. Por eso sale de la base de datos.
@Configuration
@RequiredArgsConstructor
public class ReintentoNotificacionesConfig {

    private final ReintentarNotificacionesFallidasInteractor reintentarInteractor;
    private final GestorTraza gestorTraza;

    @Value("${notificacion.reintento.max-intentos:5}")
    private int maxIntentos;

    @Value("${notificacion.reintento.limite-por-ciclo:50}")
    private int limitePorCiclo;

    @Scheduled(fixedDelayString = "${notificacion.reintento.intervalo:PT5M}")
    public void reintentarEnviosFallidos() {
        try (var alcance = gestorTraza.abrir(SolicitudTraza.paraProgramado())) {
            reintentarInteractor.ejecutar(
                    ReintentarNotificacionesFallidasCommand.crear(maxIntentos, limitePorCiclo));
        }
    }
}
