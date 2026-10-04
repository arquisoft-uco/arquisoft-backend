package com.arquisoft.fichas.application.observacionevaluacion.command.usecase.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.finder.ObservacionEvaluacionDuplicadaEnModificacionFinder;
import com.arquisoft.fichas.application.observacionevaluacion.command.finder.PertenenciaObservacionEvaluacionFinder;
import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.ObservacionEvaluacionOutputPort;
import com.arquisoft.fichas.application.observacionevaluacion.command.validator.ModificarObservacionEvaluacionValidator;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.observacionevaluacion.ModificacionObservacionEvaluacionDomain;
import com.arquisoft.fichas.domain.observacionevaluacion.exception.ObservacionEvaluacionDuplicadaException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModificarObservacionEvaluacionUseCaseImplTest {

    @Mock
    private PertenenciaObservacionEvaluacionFinder pertenenciaObservacionEvaluacionFinder;

    @Mock
    private ObservacionEvaluacionDuplicadaEnModificacionFinder observacionEvaluacionDuplicadaEnModificacionFinder;

    @Mock
    private ModificarObservacionEvaluacionValidator modificarObservacionEvaluacionValidator;

    @Mock
    private ObservacionEvaluacionOutputPort observacionEvaluacionOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ModificarObservacionEvaluacionUseCaseImpl modificarObservacionEvaluacionUseCase;

    private final UUID evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
    private final ModificacionObservacionEvaluacionDomain entrada = ModificacionObservacionEvaluacionDomain.crear(
            UtilUUID.generarNuevoUUID(), "  Nuevo texto de la observación  ", UtilUUID.generarNuevoUUID());

    @Test
    void debeActualizarElTexto_cuandoValidacionPasa() {
        // Arrange
        var pertenencia = new PertenenciaObservacionEvaluacion(evaluacionFichaPerfil, true, EstadoEvaluacion.EN_EVALUACION);
        when(pertenenciaObservacionEvaluacionFinder.obtener(entrada)).thenReturn(pertenencia);
        when(observacionEvaluacionDuplicadaEnModificacionFinder.obtener(entrada)).thenReturn(false);

        // Act
        modificarObservacionEvaluacionUseCase.ejecutar(entrada);

        // Assert — persiste solo el texto recortado de la observación pedida
        verify(observacionEvaluacionOutputPort)
                .actualizarObservacion(entrada.getObservacionEvaluacion(), "Nuevo texto de la observación");
        verify(logger).info(any(ClaveMensaje.class),
                eq(entrada.getObservacionEvaluacion()), eq(evaluacionFichaPerfil));

        // Assert — presupuesto de I/O: un viaje por finder
        verify(pertenenciaObservacionEvaluacionFinder, times(1)).obtener(entrada);
        verify(observacionEvaluacionDuplicadaEnModificacionFinder, times(1)).obtener(entrada);

        // Assert — orden: finders -> validator -> persistencia
        var orden = inOrder(pertenenciaObservacionEvaluacionFinder, observacionEvaluacionDuplicadaEnModificacionFinder,
                modificarObservacionEvaluacionValidator, observacionEvaluacionOutputPort);
        orden.verify(pertenenciaObservacionEvaluacionFinder).obtener(entrada);
        orden.verify(observacionEvaluacionDuplicadaEnModificacionFinder).obtener(entrada);
        orden.verify(modificarObservacionEvaluacionValidator).validar(entrada, true, pertenencia, false);
        orden.verify(observacionEvaluacionOutputPort).actualizarObservacion(any(), anyString());
    }

    @Test
    void noDebeActualizar_cuandoValidatorLanza() {
        // Arrange
        var pertenencia = new PertenenciaObservacionEvaluacion(evaluacionFichaPerfil, true, EstadoEvaluacion.EN_EVALUACION);
        when(pertenenciaObservacionEvaluacionFinder.obtener(entrada)).thenReturn(pertenencia);
        when(observacionEvaluacionDuplicadaEnModificacionFinder.obtener(entrada)).thenReturn(true);
        doThrow(new ObservacionEvaluacionDuplicadaException(evaluacionFichaPerfil, entrada.getObservacion()))
                .when(modificarObservacionEvaluacionValidator).validar(entrada, true, pertenencia, true);

        // Act & Assert
        assertThatThrownBy(() -> modificarObservacionEvaluacionUseCase.ejecutar(entrada))
                .isInstanceOf(ObservacionEvaluacionDuplicadaException.class);
        verify(observacionEvaluacionOutputPort, never()).actualizarObservacion(any(), any());
        verify(logger, never()).info(any(ClaveMensaje.class),
                eq(entrada.getObservacionEvaluacion()), eq(evaluacionFichaPerfil));
    }

    @Test
    void debeEntregarPertenenciaVaciaAlValidator_cuandoLaObservacionNoExiste() {
        // Arrange — el centinela llega al validator y el log de verificación no revienta con su estado VACIO
        when(pertenenciaObservacionEvaluacionFinder.obtener(entrada)).thenReturn(PertenenciaObservacionEvaluacion.VACIO);
        when(observacionEvaluacionDuplicadaEnModificacionFinder.obtener(entrada)).thenReturn(false);
        doThrow(new ObservacionEvaluacionNoEncontradaException(entrada.getObservacionEvaluacion()))
                .when(modificarObservacionEvaluacionValidator)
                .validar(entrada, false, PertenenciaObservacionEvaluacion.VACIO, false);

        // Act & Assert
        assertThatThrownBy(() -> modificarObservacionEvaluacionUseCase.ejecutar(entrada))
                .isInstanceOf(ObservacionEvaluacionNoEncontradaException.class);
        verify(observacionEvaluacionOutputPort, never()).actualizarObservacion(any(), any());
    }
}
