package com.arquisoft.artefactos.application.observacion.command.usecase.impl;

import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import com.arquisoft.artefactos.domain.observacion.AvanceObservacionesRevisionDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.AvanzarObservacionesRevisionKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AvanzarObservacionesRevisionUseCaseImplTest {

    @Mock
    private ObservacionOutputPort observacionOutputPort;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private AvanzarObservacionesRevisionUseCaseImpl useCase;

    @Test
    void debeDelegarConEstadosDelDominio_cuandoSeEjecuta() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var avance = AvanceObservacionesRevisionDomain.crear(revision);

        // Act
        useCase.ejecutar(avance);

        // Assert
        verify(observacionOutputPort).avanzarEstadoPorRevisionAsesor(revision, "NUEVA", "EN_PROGRESO");
        verify(logger).debug(AvanzarObservacionesRevisionKey.LOG_AVANZANDO, revision, "NUEVA", "EN_PROGRESO");
    }
}
