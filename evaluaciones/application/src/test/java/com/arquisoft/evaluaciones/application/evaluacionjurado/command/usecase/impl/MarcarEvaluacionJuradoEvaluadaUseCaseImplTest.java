package com.arquisoft.evaluaciones.application.evaluacionjurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.CompletitudEvaluacionJuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionJuradoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class MarcarEvaluacionJuradoEvaluadaUseCaseImplTest {

    @Mock
    private EvaluacionJuradoOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private MarcarEvaluacionJuradoEvaluadaUseCaseImpl useCase;

    @Test
    void debeActualizarAEvaluadaYLoguear_cuandoLaEvaluacionJuradoEstaCompleta() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();

        // Act
        useCase.ejecutar(CompletitudEvaluacionJuradoDomain.crear(evaluacionJurado, true));

        // Assert
        verify(outputPort).actualizarEstado(evaluacionJurado, "EVALUADA");
        verify(logger).debug(EvaluacionJuradoKey.LOG_EVALUADA, evaluacionJurado, "EVALUADA");
    }

    @Test
    void debeNoActualizar_cuandoLaEvaluacionJuradoNoEstaCompleta() {
        // Act
        useCase.ejecutar(CompletitudEvaluacionJuradoDomain.crear(UtilUUID.generarNuevoUUID(), false));

        // Assert
        verify(outputPort, never()).actualizarEstado(any(), any());
        verifyNoInteractions(logger);
    }
}
