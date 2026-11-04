package com.arquisoft.artefactos.application.observacion.command.usecase.impl;

import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import com.arquisoft.artefactos.application.observacion.command.usecase.AvanzarObservacionesRevisionUseCase;
import com.arquisoft.artefactos.domain.observacion.AvanceObservacionesRevisionDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.AvanzarObservacionesRevisionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AvanzarObservacionesRevisionUseCaseImpl implements AvanzarObservacionesRevisionUseCase {

    private final ObservacionOutputPort observacionOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(AvanceObservacionesRevisionDomain avance) {
        var estadoOrigen = avance.getEstadoOrigen().getId();
        var estadoDestino = avance.getEstadoDestino().getId();

        logger.debug(AvanzarObservacionesRevisionKey.LOG_AVANZANDO,
                avance.getRevisionAsesor(), estadoOrigen, estadoDestino);

        observacionOutputPort.avanzarEstadoPorRevisionAsesor(avance.getRevisionAsesor(), estadoOrigen, estadoDestino);
    }
}
