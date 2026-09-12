package com.arquisoft.evaluaciones.application.evaluacion.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.EvaluacionOutputPort;
import com.arquisoft.evaluaciones.application.evaluacion.command.validator.IniciarEvaluacionValidator;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.InicioEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.exception.EvaluacionFinalizadaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IniciarEvaluacionUseCaseImplTest {

    @Mock
    private EvaluacionOutputPort evaluacionOutputPort;

    @Mock
    private IniciarEvaluacionValidator iniciarEvaluacionValidator;

    @InjectMocks
    private IniciarEvaluacionUseCaseImpl useCase;

    @Test
    void debeActualizarAEnProgreso_cuandoEstadoEsPendiente() {
        // Arrange
        UUID evaluacion = UUID.randomUUID();
        var inicio = InicioEvaluacionDomain.crear(evaluacion, EstadoEvaluacion.PENDIENTE);

        // Act
        useCase.ejecutar(inicio);

        // Assert
        verify(iniciarEvaluacionValidator).validar(EstadoEvaluacion.PENDIENTE);
        verify(evaluacionOutputPort).actualizarEstado(evaluacion, EstadoEvaluacion.EN_PROGRESO.getId());
    }

    @Test
    void debeConservarEstadoSinEscribir_cuandoEstadoYaEsEnProgreso() {
        // Arrange
        UUID evaluacion = UUID.randomUUID();
        var inicio = InicioEvaluacionDomain.crear(evaluacion, EstadoEvaluacion.EN_PROGRESO);

        // Act
        useCase.ejecutar(inicio);

        // Assert
        verify(iniciarEvaluacionValidator).validar(EstadoEvaluacion.EN_PROGRESO);
        verify(evaluacionOutputPort, never()).actualizarEstado(any(), anyString());
    }

    @Test
    void debeRechazarSinEscribir_cuandoEstadoEsFinalizada() {
        // Arrange
        UUID evaluacion = UUID.randomUUID();
        var inicio = InicioEvaluacionDomain.crear(evaluacion, EstadoEvaluacion.FINALIZADA);
        doThrow(new EvaluacionFinalizadaException()).when(iniciarEvaluacionValidator).validar(EstadoEvaluacion.FINALIZADA);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(inicio)).isInstanceOf(EvaluacionFinalizadaException.class);
        verify(evaluacionOutputPort, never()).actualizarEstado(any(), anyString());
    }
}
