package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.usecase.IniciarEvaluacionUseCase;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.ContextoRegistroEvaluacionAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.ItemsEvaluacionCuantitativaAsesorRegistradosFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.model.ItemsPorEvaluacionCuantitativaAsesor;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.EvaluacionCuantitativaAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.entity.EvaluacionCuantitativaAsesorEntity;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.validator.RegistrarEvaluacionesCuantitativasAsesorValidator;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.ValoresMaximosItemsCuantitativosAsesorFinder;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.InicioEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.ContextoRegistroEvaluacionAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoPerteneceAsesorException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.EvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.RegistroEvaluacionesCuantitativasAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.event.EvaluacionesCuantitativasAsesorRegistradasEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
class RegistrarEvaluacionesCuantitativasAsesorUseCaseImplTest {

    @Mock
    private ContextoRegistroEvaluacionAsesorFinder contextoFinder;

    @Mock
    private ValoresMaximosItemsCuantitativosAsesorFinder valoresMaximosFinder;

    @Mock
    private ItemsEvaluacionCuantitativaAsesorRegistradosFinder itemsRegistradosFinder;

    @Mock
    private RegistrarEvaluacionesCuantitativasAsesorValidator validator;

    @Mock
    private EvaluacionCuantitativaAsesorOutputPort outputPort;

    @Mock
    private IniciarEvaluacionUseCase iniciarEvaluacionUseCase;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RegistrarEvaluacionesCuantitativasAsesorUseCaseImpl useCase;

    @Test
    void debeRegistrarYPublicar_cuandoTodoEsValido() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var evaluacion = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var item1 = UtilUUID.generarNuevoUUID();
        var item2 = UtilUUID.generarNuevoUUID();
        var item3 = UtilUUID.generarNuevoUUID();
        var items = Set.of(item1, item2, item3);
        var registro = registro(evaluacionAsesor, asesor, item1, item2, item3);
        var contexto = ContextoRegistroEvaluacionAsesorDomain.reconstruir(
                evaluacionAsesor, evaluacion, EstadoEvaluacion.PENDIENTE, entregable, asesor);
        var valoresMaximos = Map.of(item1, 100, item2, 100, item3, 100);
        when(contextoFinder.obtener(evaluacionAsesor)).thenReturn(contexto);
        when(valoresMaximosFinder.obtener(items)).thenReturn(valoresMaximos);
        when(itemsRegistradosFinder.obtener(new ItemsPorEvaluacionCuantitativaAsesor(evaluacionAsesor, items)))
                .thenReturn(Set.of());
        var entidadesCaptor = ArgumentCaptor.forClass(List.class);
        var inicioCaptor = ArgumentCaptor.forClass(InicioEvaluacionDomain.class);
        var eventoCaptor = ArgumentCaptor.forClass(EvaluacionesCuantitativasAsesorRegistradasEvent.class);

        // Act
        useCase.ejecutar(registro);

        // Assert
        var orden = inOrder(contextoFinder, valoresMaximosFinder, itemsRegistradosFinder,
                validator, iniciarEvaluacionUseCase, outputPort, eventPublisher);
        orden.verify(contextoFinder).obtener(evaluacionAsesor);
        orden.verify(valoresMaximosFinder).obtener(items);
        orden.verify(itemsRegistradosFinder).obtener(any(ItemsPorEvaluacionCuantitativaAsesor.class));
        orden.verify(validator).validar(any(), any(), any(), any(), any());
        orden.verify(iniciarEvaluacionUseCase).ejecutar(inicioCaptor.capture());
        orden.verify(outputPort).registrarTodas(entidadesCaptor.capture());
        orden.verify(eventPublisher).publish(eventoCaptor.capture());

        verify(contextoFinder, times(1)).obtener(any());
        verify(valoresMaximosFinder, times(1)).obtener(any());
        verify(itemsRegistradosFinder, times(1)).obtener(any());
        assertThat(inicioCaptor.getValue().getEvaluacion()).isEqualTo(evaluacion);
        assertThat(inicioCaptor.getValue().getEstadoActual()).isEqualTo(EstadoEvaluacion.PENDIENTE);
        assertThat(entidadesCaptor.getValue()).hasSize(3);
        assertThat(((EvaluacionCuantitativaAsesorEntity) entidadesCaptor.getValue().get(0)).evaluacionAsesor())
                .isEqualTo(evaluacionAsesor);
        assertThat(eventoCaptor.getValue().getEntregableId()).isEqualTo(entregable);
        verify(logger).info(any(ClaveMensaje.class), eq(evaluacionAsesor), eq(3));
        verify(logger).debug(any(ClaveMensaje.class), eq(evaluacionAsesor), eq(true), eq(3), eq(0));
        verify(logger).info(any(ClaveMensaje.class), eq(evaluacionAsesor), eq(3),
                eq(eventoCaptor.getValue().getIdEvento()));
    }

    @Test
    void debeNoPersistirNiPublicar_cuandoValidatorRechaza() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var registro = registro(evaluacionAsesor, UtilUUID.generarNuevoUUID(), item);
        when(contextoFinder.obtener(evaluacionAsesor)).thenReturn(ContextoRegistroEvaluacionAsesorDomain.VACIO);
        when(valoresMaximosFinder.obtener(Set.of(item))).thenReturn(Map.of(item, 100));
        when(itemsRegistradosFinder.obtener(any(ItemsPorEvaluacionCuantitativaAsesor.class))).thenReturn(Set.of());
        doThrow(new EvaluacionAsesorNoPerteneceAsesorException(evaluacionAsesor))
                .when(validator).validar(any(), any(), any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(registro))
                .isInstanceOf(EvaluacionAsesorNoPerteneceAsesorException.class);
        verify(iniciarEvaluacionUseCase, never()).ejecutar(any());
        verify(outputPort, never()).registrarTodas(any());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(evaluacionAsesor), eq(1), any());
    }

    private static RegistroEvaluacionesCuantitativasAsesorDomain registro(
            UUID evaluacionAsesor, UUID asesor, UUID... items) {
        var evaluaciones = Arrays.stream(items)
                .map(item -> EvaluacionCuantitativaAsesorDomain.crear(evaluacionAsesor, item, 10))
                .toList();
        return RegistroEvaluacionesCuantitativasAsesorDomain.crear(evaluacionAsesor, asesor, evaluaciones);
    }
}
