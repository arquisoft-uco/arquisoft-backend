package com.arquisoft.fichas.application.observacionevaluacion.command.usecase.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.finder.PertenenciaObservacionEvaluacionEnRemocionFinder;
import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.ObservacionEvaluacionOutputPort;
import com.arquisoft.fichas.application.observacionevaluacion.command.validator.RemoverObservacionEvaluacionValidator;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.observacionevaluacion.RemocionObservacionEvaluacionDomain;
import com.arquisoft.fichas.domain.observacionevaluacion.exception.EvaluacionFichaCerradaException;
import com.arquisoft.fichas.domain.observacionevaluacion.exception.ObservacionEvaluacionNoEncontradaException;
import com.arquisoft.fichas.domain.observacionevaluacion.model.PertenenciaObservacionEvaluacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverObservacionEvaluacionUseCaseImplTest {

    @Mock
    private PertenenciaObservacionEvaluacionEnRemocionFinder pertenenciaObservacionEvaluacionEnRemocionFinder;

    @Mock
    private RemoverObservacionEvaluacionValidator removerObservacionEvaluacionValidator;

    @Mock
    private ObservacionEvaluacionOutputPort observacionEvaluacionOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RemoverObservacionEvaluacionUseCaseImpl removerObservacionEvaluacionUseCase;

    private final UUID evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
    private final RemocionObservacionEvaluacionDomain entrada = RemocionObservacionEvaluacionDomain.crear(
            UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

    @Test
    void debeRemoverObservacion_cuandoValidacionPasa() {
        // Arrange
        var pertenencia = new PertenenciaObservacionEvaluacion(evaluacionFichaPerfil, true, EstadoEvaluacion.EN_EVALUACION);
        when(pertenenciaObservacionEvaluacionEnRemocionFinder.obtener(entrada)).thenReturn(pertenencia);

        // Act
        removerObservacionEvaluacionUseCase.ejecutar(entrada);

        // Assert
        verify(observacionEvaluacionOutputPort).removerObservacion(entrada.getObservacionEvaluacion());
        verify(logger).info(any(ClaveMensaje.class),
                eq(entrada.getObservacionEvaluacion()), eq(evaluacionFichaPerfil));

        // Assert — presupuesto de I/O: un solo viaje de lectura
        verify(pertenenciaObservacionEvaluacionEnRemocionFinder, times(1)).obtener(entrada);

        // Assert — orden: finder -> validator -> borrado
        var orden = inOrder(pertenenciaObservacionEvaluacionEnRemocionFinder,
                removerObservacionEvaluacionValidator, observacionEvaluacionOutputPort);
        orden.verify(pertenenciaObservacionEvaluacionEnRemocionFinder).obtener(entrada);
        orden.verify(removerObservacionEvaluacionValidator).validar(entrada, true, pertenencia);
        orden.verify(observacionEvaluacionOutputPort).removerObservacion(entrada.getObservacionEvaluacion());
    }

    @Test
    void noDebeRemover_cuandoValidatorLanza() {
        // Arrange
        var pertenencia = new PertenenciaObservacionEvaluacion(evaluacionFichaPerfil, true, EstadoEvaluacion.APROBADA);
        when(pertenenciaObservacionEvaluacionEnRemocionFinder.obtener(entrada)).thenReturn(pertenencia);
        doThrow(new EvaluacionFichaCerradaException(evaluacionFichaPerfil, EstadoEvaluacion.APROBADA.getId()))
                .when(removerObservacionEvaluacionValidator).validar(entrada, true, pertenencia);

        // Act & Assert
        assertThatThrownBy(() -> removerObservacionEvaluacionUseCase.ejecutar(entrada))
                .isInstanceOf(EvaluacionFichaCerradaException.class);
        verify(observacionEvaluacionOutputPort, never()).removerObservacion(any());
        verify(logger, never()).info(any(ClaveMensaje.class),
                eq(entrada.getObservacionEvaluacion()), eq(evaluacionFichaPerfil));
    }

    @Test
    void debePasarObservacionExisteFalse_cuandoFinderDevuelveVacio() {
        // Arrange — el centinela llega al validator y el log de verificación no revienta con su estado VACIO
        when(pertenenciaObservacionEvaluacionEnRemocionFinder.obtener(entrada))
                .thenReturn(PertenenciaObservacionEvaluacion.VACIO);
        doThrow(new ObservacionEvaluacionNoEncontradaException(entrada.getObservacionEvaluacion()))
                .when(removerObservacionEvaluacionValidator)
                .validar(entrada, false, PertenenciaObservacionEvaluacion.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> removerObservacionEvaluacionUseCase.ejecutar(entrada))
                .isInstanceOf(ObservacionEvaluacionNoEncontradaException.class);
        verify(observacionEvaluacionOutputPort, never()).removerObservacion(any());
    }
}
