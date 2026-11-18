package com.arquisoft.evaluaciones.application.observacionitemasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.ContextoEvaluacionCuantitativaAsesorPorObservacionFinder;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.finder.OtraObservacionItemAsesorConDescripcionExisteFinder;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.secondaryport.ObservacionItemAsesorOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.validator.ModificarObservacionItemAsesorValidator;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.ContextoEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.ModificacionObservacionItemAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.ModificacionObservacionItemAsesorFinalizadaException;
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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModificarObservacionItemAsesorUseCaseImplTest {

    @Mock
    private ContextoEvaluacionCuantitativaAsesorPorObservacionFinder contextoPorObservacionFinder;

    @Mock
    private OtraObservacionItemAsesorConDescripcionExisteFinder otraObservacionConDescripcionExisteFinder;

    @Mock
    private ModificarObservacionItemAsesorValidator validator;

    @Mock
    private ObservacionItemAsesorOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ModificarObservacionItemAsesorUseCaseImpl useCase;

    @Test
    void debeActualizarDescripcion_cuandoValidacionPasa() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var modificacion = ModificacionObservacionItemAsesorDomain.crear(observacion, asesor, "Nuevo sustento");
        var contexto = ContextoEvaluacionCuantitativaAsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), asesor, EstadoEvaluacion.EN_PROGRESO);
        when(contextoPorObservacionFinder.obtener(observacion)).thenReturn(contexto);
        when(otraObservacionConDescripcionExisteFinder.obtener(modificacion)).thenReturn(false);

        // Act
        useCase.ejecutar(modificacion);

        // Assert
        var orden = inOrder(contextoPorObservacionFinder, otraObservacionConDescripcionExisteFinder, validator,
                outputPort);
        orden.verify(contextoPorObservacionFinder).obtener(observacion);
        orden.verify(otraObservacionConDescripcionExisteFinder).obtener(modificacion);
        orden.verify(validator).validar(modificacion, contexto, false);
        orden.verify(outputPort).actualizarDescripcion(observacion, "Nuevo sustento");
        verify(contextoPorObservacionFinder, times(1)).obtener(any());
        verify(otraObservacionConDescripcionExisteFinder, times(1)).obtener(any());
        verify(logger).debug(any(ClaveMensaje.class), eq(true), eq(EstadoEvaluacion.EN_PROGRESO.getId()), eq(false));
        verify(logger, times(2)).info(any(ClaveMensaje.class), eq(observacion));
    }

    @Test
    void noDebeActualizar_cuandoValidatorRechaza() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var modificacion = ModificacionObservacionItemAsesorDomain.crear(
                observacion, UtilUUID.generarNuevoUUID(), "Nuevo sustento");
        when(contextoPorObservacionFinder.obtener(observacion))
                .thenReturn(ContextoEvaluacionCuantitativaAsesorDomain.VACIO);
        when(otraObservacionConDescripcionExisteFinder.obtener(modificacion)).thenReturn(false);
        doThrow(new ModificacionObservacionItemAsesorFinalizadaException(UtilUUID.generarNuevoUUID()))
                .when(validator).validar(any(), any(), anyBoolean());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(modificacion))
                .isInstanceOf(ModificacionObservacionItemAsesorFinalizadaException.class);
        verify(outputPort, never()).actualizarDescripcion(any(), any());
        verify(logger, times(1)).info(any(ClaveMensaje.class), eq(observacion));
    }
}
