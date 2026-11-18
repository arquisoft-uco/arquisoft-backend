package com.arquisoft.evaluaciones.application.evaluacionjurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.finder.ContextoAsignacionEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.JuradosAsignadosFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.JuradosConEvaluacionesFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.model.CriterioJuradosEvaluacion;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.validator.RemoverJuradosEvaluacionValidator;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.ContextoAsignacionEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.model.EstadoAsignacionEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ExistenciaEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.RemocionJuradosEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.exception.JuradosConEvaluacionesException;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.EvaluacionesRegistradasJurados;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.JuradosAsignadosEvaluacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionJuradoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

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
class RemoverJuradosEvaluacionUseCaseImplTest {

    @Mock
    private ContextoAsignacionEvaluacionFinder contextoFinder;

    @Mock
    private JuradosAsignadosFinder asignadosFinder;

    @Mock
    private JuradosConEvaluacionesFinder conEvaluacionesFinder;

    @Mock
    private RemoverJuradosEvaluacionValidator validator;

    @Mock
    private EvaluacionJuradoOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RemoverJuradosEvaluacionUseCaseImpl useCase;

    @Test
    void debeEliminarElLoteEnUnaSolaLlamada_cuandoLaValidacionPasa() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var remocion = RemocionJuradosEvaluacionDomain.crear(evaluacion, List.of(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID()));
        var solicitados = remocion.getJurados();
        var criterio = new CriterioJuradosEvaluacion(evaluacion, solicitados);
        when(contextoFinder.obtener(evaluacion)).thenReturn(
                ContextoAsignacionEvaluacionDomain.reconstruir(evaluacion, EstadoEvaluacion.EN_PROGRESO));
        when(asignadosFinder.obtener(criterio)).thenReturn(solicitados);
        when(conEvaluacionesFinder.obtener(criterio)).thenReturn(Set.of());

        // Act
        useCase.ejecutar(remocion);

        // Assert
        var orden = inOrder(logger, contextoFinder, asignadosFinder, conEvaluacionesFinder, validator, outputPort);
        orden.verify(logger).info(eq(EvaluacionJuradoKey.LOG_REMOVIENDO), eq(evaluacion), eq(3));
        orden.verify(contextoFinder, times(1)).obtener(evaluacion);
        orden.verify(asignadosFinder, times(1)).obtener(criterio);
        orden.verify(conEvaluacionesFinder, times(1)).obtener(criterio);
        orden.verify(validator).validar(
                eq(new ExistenciaEvaluacion(evaluacion, true)),
                eq(new EstadoAsignacionEvaluacion(EstadoEvaluacion.EN_PROGRESO)),
                eq(new JuradosAsignadosEvaluacion(solicitados, solicitados)),
                eq(new EvaluacionesRegistradasJurados(Set.of())));
        orden.verify(outputPort).eliminarPorEvaluacionYJurados(evaluacion, solicitados);
        orden.verify(logger).info(eq(EvaluacionJuradoKey.LOG_REMOVIDOS), eq(evaluacion), eq(3));
        assertThat(solicitados).hasSize(3);
    }

    @Test
    void noDebeEliminarNiCerrar_cuandoElValidatorRechaza() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var remocion = RemocionJuradosEvaluacionDomain.crear(evaluacion, List.of(jurado));
        when(contextoFinder.obtener(evaluacion)).thenReturn(
                ContextoAsignacionEvaluacionDomain.reconstruir(evaluacion, EstadoEvaluacion.PENDIENTE));
        when(asignadosFinder.obtener(any(CriterioJuradosEvaluacion.class))).thenReturn(Set.of(jurado));
        when(conEvaluacionesFinder.obtener(any(CriterioJuradosEvaluacion.class))).thenReturn(Set.of(jurado));
        doThrow(new JuradosConEvaluacionesException(Set.of(jurado))).when(validator).validar(
                any(ExistenciaEvaluacion.class),
                any(EstadoAsignacionEvaluacion.class),
                any(JuradosAsignadosEvaluacion.class),
                any(EvaluacionesRegistradasJurados.class));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(remocion)).isInstanceOf(JuradosConEvaluacionesException.class);
        verify(outputPort, never()).eliminarPorEvaluacionYJurados(any(), any());
        verify(logger, never()).info(eq(EvaluacionJuradoKey.LOG_REMOVIDOS), eq(evaluacion), eq(1));
    }
}
