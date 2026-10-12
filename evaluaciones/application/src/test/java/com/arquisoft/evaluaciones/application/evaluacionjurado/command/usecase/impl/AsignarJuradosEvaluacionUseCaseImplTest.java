package com.arquisoft.evaluaciones.application.evaluacionjurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.finder.ContextoAsignacionEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.JuradosAsignadosFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.model.CriterioJuradosEvaluacion;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.validator.AsignarJuradosEvaluacionValidator;
import com.arquisoft.evaluaciones.application.jurado.command.finder.JuradosVigentesFinder;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.ContextoAsignacionEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.model.EstadoRegistroEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ExistenciaEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.AsignacionJuradosEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.EvaluacionJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.exception.JuradosYaAsignadosException;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.DisponibilidadJuradosEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.ExistenciaJurados;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionJuradoKey;
import com.arquisoft.shared.util.UtilUUID;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignarJuradosEvaluacionUseCaseImplTest {

    @Mock
    private ContextoAsignacionEvaluacionFinder contextoFinder;

    @Mock
    private JuradosVigentesFinder vigentesFinder;

    @Mock
    private JuradosAsignadosFinder asignadosFinder;

    @Mock
    private AsignarJuradosEvaluacionValidator validator;

    @Mock
    private EvaluacionJuradoOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AsignarJuradosEvaluacionUseCaseImpl useCase;

    @Test
    void debeRegistrarUnaFilaPorJuradoYDevolverIds_cuandoLaAsignacionEsValida() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var jurados = List.of(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var asignacion = asignacion(evaluacion, jurados);
        var solicitados = asignacion.getJurados();
        when(contextoFinder.obtener(evaluacion)).thenReturn(
                ContextoAsignacionEvaluacionDomain.reconstruir(evaluacion, EstadoEvaluacion.PENDIENTE));
        when(vigentesFinder.obtener(solicitados)).thenReturn(solicitados);
        when(asignadosFinder.obtener(new CriterioJuradosEvaluacion(evaluacion, solicitados))).thenReturn(Set.of());

        // Act
        var ids = useCase.ejecutar(asignacion);

        // Assert
        var orden = inOrder(contextoFinder, vigentesFinder, asignadosFinder, validator, outputPort, logger);
        orden.verify(logger).info(eq(EvaluacionJuradoKey.LOG_ASIGNANDO), eq(evaluacion), eq(3));
        orden.verify(contextoFinder, times(1)).obtener(evaluacion);
        orden.verify(vigentesFinder, times(1)).obtener(solicitados);
        orden.verify(asignadosFinder, times(1)).obtener(any(CriterioJuradosEvaluacion.class));
        orden.verify(validator).validar(
                any(ExistenciaEvaluacion.class),
                any(EstadoRegistroEvaluacion.class),
                any(ExistenciaJurados.class),
                any(DisponibilidadJuradosEvaluacion.class));
        orden.verify(outputPort).registrarTodas(anyList());
        orden.verify(logger).info(eq(EvaluacionJuradoKey.LOG_ASIGNADOS), eq(evaluacion), eq(3));

        var captor = ArgumentCaptor.<List<EvaluacionJuradoEntity>>captor();
        verify(outputPort).registrarTodas(captor.capture());
        assertThat(captor.getValue()).hasSize(3)
                .allSatisfy(entidad -> assertThat(entidad.evaluacion()).isEqualTo(evaluacion))
                .extracting(EvaluacionJuradoEntity::jurado)
                .containsExactlyInAnyOrderElementsOf(jurados);
        assertThat(ids).containsExactlyElementsOf(
                captor.getValue().stream().map(EvaluacionJuradoEntity::id).toList());
    }

    @Test
    void debePasarAlValidatorElDatoCrudoDeLosFinders_cuandoLaEvaluacionNoExiste() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var asignacion = asignacion(evaluacion, List.of(jurado));
        when(contextoFinder.obtener(evaluacion)).thenReturn(ContextoAsignacionEvaluacionDomain.VACIO);
        when(vigentesFinder.obtener(Set.of(jurado))).thenReturn(Set.of());
        when(asignadosFinder.obtener(any(CriterioJuradosEvaluacion.class))).thenReturn(Set.of());

        // Act
        useCase.ejecutar(asignacion);

        // Assert
        var existencia = ArgumentCaptor.forClass(ExistenciaEvaluacion.class);
        var jurados = ArgumentCaptor.forClass(ExistenciaJurados.class);
        verify(validator).validar(existencia.capture(), any(EstadoRegistroEvaluacion.class),
                jurados.capture(), any(DisponibilidadJuradosEvaluacion.class));
        assertThat(existencia.getValue().existe()).isFalse();
        assertThat(jurados.getValue().existentes()).isEmpty();
    }

    @Test
    void noDebePersistirNiCerrar_cuandoElValidatorRechaza() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var asignacion = asignacion(evaluacion, List.of(jurado));
        when(contextoFinder.obtener(evaluacion)).thenReturn(
                ContextoAsignacionEvaluacionDomain.reconstruir(evaluacion, EstadoEvaluacion.EN_PROGRESO));
        when(vigentesFinder.obtener(Set.of(jurado))).thenReturn(Set.of(jurado));
        when(asignadosFinder.obtener(any(CriterioJuradosEvaluacion.class))).thenReturn(Set.of(jurado));
        doThrow(new JuradosYaAsignadosException(Set.of(jurado))).when(validator).validar(
                any(ExistenciaEvaluacion.class),
                any(EstadoRegistroEvaluacion.class),
                any(ExistenciaJurados.class),
                any(DisponibilidadJuradosEvaluacion.class));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(asignacion)).isInstanceOf(JuradosYaAsignadosException.class);
        verify(outputPort, never()).registrarTodas(anyList());
        verify(logger, never()).info(eq(EvaluacionJuradoKey.LOG_ASIGNADOS), eq(evaluacion), eq(1));
    }

    private AsignacionJuradosEvaluacionDomain asignacion(UUID evaluacion, List<UUID> jurados) {
        var evaluacionesJurado = jurados.stream()
                .map(jurado -> EvaluacionJuradoDomain.crear(evaluacion, jurado))
                .toList();
        return AsignacionJuradosEvaluacionDomain.crear(evaluacion, evaluacionesJurado);
    }
}
