package com.arquisoft.seguridad.infrastructure.mapeoroles.command.primaryadapter.scheduler;

import com.arquisoft.seguridad.application.mapeoroles.command.primaryport.interactor.ActualizarMapeoRolesInteractor;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.MapeoRolesKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActualizarMapeoRolesScheduler {

    private final ActualizarMapeoRolesInteractor actualizarMapeoRolesInteractor;
    private final AutenticacionProperties propiedadesAutenticacion;
    private final GestorTraza gestorTraza;
    private final AppLogger logger;

    @Scheduled(fixedDelayString = "${security.authorization.role-expansion.check-interval:PT30S}")
    public void actualizar() {
        try (var alcance = gestorTraza.abrir(SolicitudTraza.paraProgramado())) {
            try {
                if (usaTablaDeEquivalencias()) {
                    actualizarMapeoRolesInteractor.ejecutar();
                }
            } catch (RuntimeException e) {
                logger.error(MapeoRolesKey.LOG_REFRESCO_INESPERADO, e, e.getMessage());
            }
        }
    }

    private boolean usaTablaDeEquivalencias() {
        return propiedadesAutenticacion.habilitados().stream()
                .anyMatch(emisor -> emisor.authorization() == ModoAutorizacion.ROLE_TABLE);
    }
}
