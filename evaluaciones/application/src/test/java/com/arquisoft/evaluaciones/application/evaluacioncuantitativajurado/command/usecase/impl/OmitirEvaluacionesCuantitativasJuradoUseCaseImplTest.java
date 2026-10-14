package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.EvaluacionesCuantitativasJuradoDeEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.EvaluacionCuantitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator.OmitirEvaluacionesCuantitativasJuradoValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.ContextoRegistroEvaluacionJuradoFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.ObservacionesDeEvaluacionesCuantitativasExistenFinder;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.OmisionEvaluacionesCuantitativasJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionesCuantitativasJuradoConObservacionesException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionesCuantitativasJuradoNoEncontradasException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.OmisionEvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.EstadoOmisionEvaluacionesCuantitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ExistenciaEvaluacionesCuantitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ObservacionesEvaluacionesCuantitativasJurado;
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
class OmitirEvaluacionesCuantitativasJuradoUseCaseImplTest {

    @Mock
    private ContextoRegistroEvaluacionJuradoFinder contextoFinder;

    @Mock
    private EvaluacionesCuantitativasJuradoDeEvaluacionFinder evaluacionesFinder;

    @Mock
    private ObservacionesDeEvaluacionesCuantitativasExistenFinder observacionesFinder;

    @Mock
    private OmitirEvaluacionesCuantitativasJuradoValidator validator;

    @Mock
    private EvaluacionCuantitativaJuradoOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private OmitirEvaluacionesCuantitativasJuradoUseCaseImpl useCase;

    @Test
    void debeValidarYEliminarElLote_cuandoLaEvaluacionExisteContieneLosIdsNoEstaFinalizadaYNoTieneObservaciones() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var ids = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        var omision = OmisionEvaluacionesCuantitativasJuradoDomain.crear(evaluacionJurado, ids);
        var contexto = ContextoRegistroEvaluacionJuradoDomain.reconstruir(
                evaluacionJurado, UUID.randomUUID(), EstadoEvaluacion.EN_PROGRESO, UUID.randomUUID());
        when(contextoFinder.obtener(evaluacionJurado)).thenReturn(contexto);
        when(evaluacionesFinder.obtener(omision)).thenReturn(Set.copyOf(ids));
        when(observacionesFinder.obtener(Set.copyOf(ids))).thenReturn(false);

        // Act
        useCase.ejecutar(omision);

        // Assert
        var orden = inOrder(contextoFinder, evaluacionesFinder, observacionesFinder, validator, outputPort);
        orden.verify(contextoFinder).obtener(evaluacionJurado);
        orden.verify(evaluacionesFinder).obtener(omision);
        orden.verify(observacionesFinder).obtener(Set.copyOf(ids));
        orden.verify(validator).validar(any(), any(), any(), any());
        orden.verify(outputPort).eliminarPorIds(evaluacionJurado, Set.copyOf(ids));

        verify(contextoFinder, times(1)).obtener(any());
        verify(evaluacionesFinder, times(1)).obtener(any());
        verify(observacionesFinder, times(1)).obtener(any());
        verify(outputPort, times(1)).eliminarPorIds(any(), any());

        var existenciaJurado = ArgumentCaptor.forClass(ExistenciaEvaluacionJurado.class);
        var existenciaEvaluaciones = ArgumentCaptor.forClass(ExistenciaEvaluacionesCuantitativasJurado.class);
        var estado = ArgumentCaptor.forClass(EstadoOmisionEvaluacionesCuantitativasJurado.class);
        var observaciones = ArgumentCaptor.forClass(ObservacionesEvaluacionesCuantitativasJurado.class);
        verify(validator).validar(
                existenciaJurado.capture(), existenciaEvaluaciones.capture(), estado.capture(), observaciones.capture());
        assertThat(existenciaJurado.getValue().existe()).isTrue();
        assertThat(existenciaJurado.getValue().evaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(existenciaEvaluaciones.getValue().evaluacionesSolicitadas()).containsExactlyInAnyOrderElementsOf(ids);
        assertThat(existenciaEvaluaciones.getValue().evaluacionesEncontradas()).containsExactlyInAnyOrderElementsOf(ids);
        assertThat(estado.getValue().estado()).isEqualTo(EstadoEvaluacion.EN_PROGRESO);
        assertThat(observaciones.getValue().existenObservaciones()).isFalse();
        assertThat(observaciones.getValue().evaluaciones()).containsExactlyInAnyOrderElementsOf(ids);

        verify(logger, times(2)).info(any(ClaveMensaje.class), eq(evaluacionJurado), eq(3));
        verify(logger).debug(any(ClaveMensaje.class), eq(evaluacionJurado), eq(true), eq("EN_PROGRESO"), eq(3), eq(false));
    }

    @Test
    void debeAbortarSinEliminar_cuandoLaEvaluacionDeJuradoNoExiste() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var omision = OmisionEvaluacionesCuantitativasJuradoDomain.crear(evaluacionJurado, List.of(UUID.randomUUID()));
        when(contextoFinder.obtener(evaluacionJurado)).thenReturn(ContextoRegistroEvaluacionJuradoDomain.VACIO);
        when(evaluacionesFinder.obtener(omision)).thenReturn(Set.of());
        when(observacionesFinder.obtener(any())).thenReturn(false);
        doThrow(new EvaluacionJuradoNoEncontradaException(evaluacionJurado))
                .when(validator).validar(any(), any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(omision))
                .isInstanceOf(EvaluacionJuradoNoEncontradaException.class);
        var captor = ArgumentCaptor.forClass(ExistenciaEvaluacionJurado.class);
        verify(validator).validar(captor.capture(), any(), any(), any());
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
        var omision = OmisionEvaluacionesCuantitativasJuradoDomain.crear(evaluacionJurado, List.of(propia, ajena));
        var contexto = ContextoRegistroEvaluacionJuradoDomain.reconstruir(
                evaluacionJurado, UUID.randomUUID(), EstadoEvaluacion.PENDIENTE, UUID.randomUUID());
        when(contextoFinder.obtener(evaluacionJurado)).thenReturn(contexto);
        when(evaluacionesFinder.obtener(omision)).thenReturn(Set.of(propia));
        when(observacionesFinder.obtener(any())).thenReturn(false);
        doThrow(new EvaluacionesCuantitativasJuradoNoEncontradasException(Set.of(ajena), evaluacionJurado))
                .when(validator).validar(any(), any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(omision))
                .isInstanceOf(EvaluacionesCuantitativasJuradoNoEncontradasException.class);
        verify(outputPort, never()).eliminarPorIds(any(), any());
    }

    @Test
    void debeAbortarSinEliminar_cuandoLaEvaluacionEstaFinalizada() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var evaluacion = UUID.randomUUID();
        var omision = OmisionEvaluacionesCuantitativasJuradoDomain.crear(evaluacionJurado, List.of(evaluacion));
        var contexto = ContextoRegistroEvaluacionJuradoDomain.reconstruir(
                evaluacionJurado, UUID.randomUUID(), EstadoEvaluacion.FINALIZADA, UUID.randomUUID());
        when(contextoFinder.obtener(evaluacionJurado)).thenReturn(contexto);
        when(evaluacionesFinder.obtener(omision)).thenReturn(Set.of(evaluacion));
        when(observacionesFinder.obtener(any())).thenReturn(false);
        doThrow(new OmisionEvaluacionJuradoFinalizadaException(evaluacionJurado))
                .when(validator).validar(any(), any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(omision))
                .isInstanceOf(OmisionEvaluacionJuradoFinalizadaException.class);
        var captor = ArgumentCaptor.forClass(EstadoOmisionEvaluacionesCuantitativasJurado.class);
        verify(validator).validar(any(), any(), captor.capture(), any());
        assertThat(captor.getValue().estado()).isEqualTo(EstadoEvaluacion.FINALIZADA);
        verify(outputPort, never()).eliminarPorIds(any(), any());
    }

    @Test
    void debeAbortarSinEliminarNinguna_cuandoAlgunaEvaluacionTieneObservaciones() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var ids = List.of(UUID.randomUUID(), UUID.randomUUID());
        var omision = OmisionEvaluacionesCuantitativasJuradoDomain.crear(evaluacionJurado, ids);
        var contexto = ContextoRegistroEvaluacionJuradoDomain.reconstruir(
                evaluacionJurado, UUID.randomUUID(), EstadoEvaluacion.EN_PROGRESO, UUID.randomUUID());
        when(contextoFinder.obtener(evaluacionJurado)).thenReturn(contexto);
        when(evaluacionesFinder.obtener(omision)).thenReturn(Set.copyOf(ids));
        when(observacionesFinder.obtener(Set.copyOf(ids))).thenReturn(true);
        doThrow(new EvaluacionesCuantitativasJuradoConObservacionesException(Set.copyOf(ids), evaluacionJurado))
                .when(validator).validar(any(), any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(omision))
                .isInstanceOf(EvaluacionesCuantitativasJuradoConObservacionesException.class);
        var captor = ArgumentCaptor.forClass(ObservacionesEvaluacionesCuantitativasJurado.class);
        verify(validator).validar(any(), any(), any(), captor.capture());
        assertThat(captor.getValue().existenObservaciones()).isTrue();
        verify(outputPort, never()).eliminarPorIds(any(), any());
        verify(logger, times(1)).info(any(ClaveMensaje.class), eq(evaluacionJurado), eq(2));
    }
}
