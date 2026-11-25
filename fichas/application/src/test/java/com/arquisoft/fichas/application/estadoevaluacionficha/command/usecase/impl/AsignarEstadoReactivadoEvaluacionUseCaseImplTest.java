package com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AsignarEstadoReactivadoEvaluacionUseCaseImplTest {

    @Mock
    private EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AsignarEstadoReactivadoEvaluacionUseCaseImpl useCase;

    @Test
    void debePersistirEstadoEnEvaluacion_cuandoSeEjecuta() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var estado = EstadoEvaluacionFichaDomain.crearConEstado(evaluacion, EstadoEvaluacion.EN_EVALUACION.getId());

        // Act
        useCase.ejecutar(estado);

        // Assert
        verify(estadoEvaluacionFichaOutputPort).agregarEstado(argThat(entity ->
                entity.id().equals(estado.getId())
                        && entity.evaluacionFichaPerfil().equals(evaluacion)
                        && entity.estadoEvaluacion().equals(EstadoEvaluacion.EN_EVALUACION.getId())));
    }
}
