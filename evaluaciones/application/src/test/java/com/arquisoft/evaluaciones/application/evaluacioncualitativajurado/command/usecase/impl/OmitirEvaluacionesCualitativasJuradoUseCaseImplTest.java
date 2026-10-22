package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.EvaluacionesCualitativasJuradoDeEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.EvaluacionCualitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.validator.OmitirEvaluacionesCualitativasJuradoValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.ContextoRegistroEvaluacionJuradoFinder;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.OmisionEvaluacionesCualitativasJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionesCualitativasJuradoNoEncontradasException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.EstadoOmisionEvaluacionesCualitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionesCualitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.ContextoRegistroEvaluacionJuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
class OmitirEvaluacionesCualitativasJuradoUseCaseImplTest {

    @Mock
    private ContextoRegistroEvaluacionJuradoFinder contextoFinder;

    @Mock
    private EvaluacionesCualitativasJuradoDeEvaluacionFinder evaluacionesFinder;

    @Mock
    private OmitirEvaluacionesCualitativasJuradoValidator validator;

    @Mock
    private EvaluacionCualitativaJuradoOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private OmitirEvaluacionesCualitativasJuradoUseCaseImpl useCase;

    @Test
    void debeValidarYEliminarElLote_cuandoLaEvaluacionExisteContieneLosIdsYNoEstaFinalizada() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var ids = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        var omision = OmisionEvaluacionesCualitativasJuradoDomain.crear(evaluacionJurado, ids);
        var contexto = ContextoRegistroEvaluacionJuradoDomain.reconstruir(
                evaluacionJurado, UUID.randomUUID(), EstadoEvaluacion.EN_PROGRESO, UUID.randomUUID());
        when(contextoFinder.obtener(evaluacionJurado)).thenReturn(contexto);
        when(evaluacionesFinder.obtener(omision)).thenReturn(Set.copyOf(ids));

        // Act
        useCase.ejecutar(omision);

        // Assert
        var orden = inOrder(contextoFinder, evaluacionesFinder, validator, outputPort);
        orden.verify(contextoFinder).obtener(evaluacionJurado);
        orden.verify(evaluacionesFinder).obtener(omision);
        orden.verify(validator).validar(any(), any(), any());
        orden.verify(outputPort).eliminarPorIds(evaluacionJurado, Set.copyOf(ids));

        verify(contextoFinder, times(1)).obtener(any());
        verify(evaluacionesFinder, times(1)).obtener(any());
        verify(outputPort, times(1)).eliminarPorIds(any(), any());

        var existenciaJurado = ArgumentCaptor.forClass(ExistenciaEvaluacionJurado.class);
        var existenciaEvaluaciones = ArgumentCaptor.forClass(ExistenciaEvaluacionesCualitativasJurado.class);
        var estado = ArgumentCaptor.forClass(EstadoOmisionEvaluacionesCualitativasJurado.class);
        verify(validator).validar(existenciaJurado.capture(), existenciaEvaluaciones.capture(), estado.capture());
        assertThat(existenciaJurado.getValue().existe()).isTrue();
        assertThat(existenciaJurado.getValue().evaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(existenciaEvaluaciones.getValue().evaluacionesSolicitadas()).containsExactlyInAnyOrderElementsOf(ids);
        assertThat(existenciaEvaluaciones.getValue().evaluacionesEncontradas()).containsExactlyInAnyOrderElementsOf(ids);
        assertThat(estado.getValue().estado()).isEqualTo(EstadoEvaluacion.EN_PROGRESO);

        verify(logger, times(2)).info(any(ClaveMensaje.class), eq(evaluacionJurado), eq(3));
        verify(logger).debug(any(ClaveMensaje.class), eq(evaluacionJurado), eq(true), eq("EN_PROGRESO"), eq(3));
    }

    @Test
    void debeAbortarSinEliminar_cuandoLaEvaluacionDeJuradoNoExiste() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var omision = OmisionEvaluacionesCualitativasJuradoDomain.crear(evaluacionJurado, List.of(UUID.randomUUID()));
        when(contextoFinder.obtener(evaluacionJurado)).thenReturn(ContextoRegistroEvaluacionJuradoDomain.VACIO);
        when(evaluacionesFinder.obtener(omision)).thenReturn(Set.of());
        doThrow(new EvaluacionJuradoNoEncontradaException(evaluacionJurado)).when(validator).validar(any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(omision))
                .isInstanceOf(EvaluacionJuradoNoEncontradaException.class);
        var captor = ArgumentCaptor.forClass(ExistenciaEvaluacionJurado.class);
        verify(validator).validar(captor.capture(), any(), any());
        assertThat(captor.getValue().existe()).isFalse();
        verify(outputPort, never()).eliminarPorIds(any(), any());
        verify(logger, times(1)).info(any(ClaveMensaje.class), eq(evaluacionJurado), eq(1));
    }

    @Test
    void debeAbortarSinEliminar_cuandoAlgunIdNoPerteneceALaEvaluacionJurado() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var propia = UUID.randomUUID();
        var ajena = UUID.randomUUID();
        var omision = OmisionEvaluacionesCualitativasJuradoDomain.crear(evaluacionJurado, List.of(propia, ajena));
        var contexto = ContextoRegistroEvaluacionJuradoDomain.reconstruir(
                evaluacionJurado, UUID.randomUUID(), EstadoEvaluacion.PENDIENTE, UUID.randomUUID());
        when(contextoFinder.obtener(evaluacionJurado)).thenReturn(contexto);
        when(evaluacionesFinder.obtener(omision)).thenReturn(Set.of(propia));
        doThrow(new EvaluacionesCualitativasJuradoNoEncontradasException(Set.of(ajena), evaluacionJurado))
                .when(validator).validar(any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(omision))
                .isInstanceOf(EvaluacionesCualitativasJuradoNoEncontradasException.class);
        verify(outputPort, never()).eliminarPorIds(any(), any());
    }

    @Test
    void debeAbortarSinEliminar_cuandoLaEvaluacionEstaFinalizada() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var evaluacion = UUID.randomUUID();
        var omision = OmisionEvaluacionesCualitativasJuradoDomain.crear(evaluacionJurado, List.of(evaluacion));
        var contexto = ContextoRegistroEvaluacionJuradoDomain.reconstruir(
                evaluacionJurado, UUID.randomUUID(), EstadoEvaluacion.FINALIZADA, UUID.randomUUID());
        when(contextoFinder.obtener(evaluacionJurado)).thenReturn(contexto);
        when(evaluacionesFinder.obtener(omision)).thenReturn(Set.of(evaluacion));
        doThrow(new EvaluacionJuradoFinalizadaException(evaluacionJurado)).when(validator).validar(any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(omision))
                .isInstanceOf(EvaluacionJuradoFinalizadaException.class);
        var captor = ArgumentCaptor.forClass(EstadoOmisionEvaluacionesCualitativasJurado.class);
        verify(validator).validar(any(), any(), captor.capture());
        assertThat(captor.getValue().estado()).isEqualTo(EstadoEvaluacion.FINALIZADA);
        verify(outputPort, never()).eliminarPorIds(any(), any());
    }
}
