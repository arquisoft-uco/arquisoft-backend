package com.arquisoft.evaluaciones.application.observacionitemasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.ContextoEvaluacionCuantitativaAsesorPorObservacionFinder;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.secondaryport.ObservacionItemAsesorOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.validator.RemoverObservacionItemAsesorValidator;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.ContextoEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.RemocionObservacionItemAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.RemocionObservacionItemAsesorFinalizadaException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class RemoverObservacionItemAsesorUseCaseImplTest {

    @Mock
    private ContextoEvaluacionCuantitativaAsesorPorObservacionFinder contextoPorObservacionFinder;

    @Mock
    private RemoverObservacionItemAsesorValidator validator;

    @Mock
    private ObservacionItemAsesorOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RemoverObservacionItemAsesorUseCaseImpl useCase;

    @Test
    void debeEliminarYRegistrarLog_cuandoValidacionPasa() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var remocion = RemocionObservacionItemAsesorDomain.crear(observacion, asesor);
        var contexto = ContextoEvaluacionCuantitativaAsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), asesor, EstadoEvaluacion.EN_PROGRESO);
        when(contextoPorObservacionFinder.obtener(observacion)).thenReturn(contexto);

        // Act
        useCase.ejecutar(remocion);

        // Assert
        var orden = inOrder(contextoPorObservacionFinder, validator, outputPort);
        orden.verify(contextoPorObservacionFinder).obtener(observacion);
        orden.verify(validator).validar(remocion, contexto);
        orden.verify(outputPort).eliminar(observacion);
        verify(contextoPorObservacionFinder, times(1)).obtener(any());
        verify(logger).debug(any(ClaveMensaje.class), eq(true), eq(EstadoEvaluacion.EN_PROGRESO.getId()));
        verify(logger, times(2)).info(any(ClaveMensaje.class), eq(observacion));
    }

    @Test
    void noDebeEliminar_cuandoValidatorRechaza() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var remocion = RemocionObservacionItemAsesorDomain.crear(observacion, UtilUUID.generarNuevoUUID());
        when(contextoPorObservacionFinder.obtener(observacion))
                .thenReturn(ContextoEvaluacionCuantitativaAsesorDomain.VACIO);
        doThrow(new RemocionObservacionItemAsesorFinalizadaException(UtilUUID.generarNuevoUUID()))
                .when(validator).validar(any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(remocion))
                .isInstanceOf(RemocionObservacionItemAsesorFinalizadaException.class);
        verify(outputPort, never()).eliminar(any());
        verify(logger, times(1)).info(any(ClaveMensaje.class), eq(observacion));
    }
}
