package com.arquisoft.evaluaciones.application.evaluacionasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.asesor.command.finder.AsesorVigenteExisteFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.finder.ContextoAsignacionEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.EvaluacionAsesorConRegistrosFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.EvaluacionAsesorPorEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.EvaluacionAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.entity.EvaluacionAsesorEntity;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.validator.AsignarAsesorEvaluacionValidator;
import com.arquisoft.evaluaciones.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.evaluaciones.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.ContextoAsignacionEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.model.EstadoAsignacionEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ExistenciaEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.EvaluacionAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.model.CambioAsesorEvaluacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionAsesorKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class AsignarAsesorEvaluacionUseCaseImplTest {

    @Mock
    private ContextoAsignacionEvaluacionFinder contextoAsignacionEvaluacionFinder;

    @Mock
    private AsesorVigenteExisteFinder asesorVigenteExisteFinder;

    @Mock
    private EvaluacionAsesorPorEvaluacionFinder evaluacionAsesorPorEvaluacionFinder;

    @Mock
    private EvaluacionAsesorConRegistrosFinder evaluacionAsesorConRegistrosFinder;

    @Mock
    private AsignarAsesorEvaluacionValidator validator;

    @Mock
    private EvaluacionAsesorOutputPort evaluacionAsesorOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AsignarAsesorEvaluacionUseCaseImpl useCase;

    private UUID evaluacion;
    private UUID asesorNuevo;
    private EvaluacionAsesorDomain solicitud;
    private ContextoAsignacionEvaluacionDomain contexto;

    @BeforeEach
    void setUp() {
        evaluacion = UtilUUID.generarNuevoUUID();
        asesorNuevo = UtilUUID.generarNuevoUUID();
        solicitud = EvaluacionAsesorDomain.crear(evaluacion, asesorNuevo);
        contexto = ContextoAsignacionEvaluacionDomain.reconstruir(evaluacion, EstadoEvaluacion.PENDIENTE);
        when(contextoAsignacionEvaluacionFinder.obtener(evaluacion)).thenReturn(contexto);
        when(asesorVigenteExisteFinder.obtener(asesorNuevo)).thenReturn(true);
        when(evaluacionAsesorConRegistrosFinder.obtener(evaluacion)).thenReturn(false);
    }

    @Test
    void debeRegistrar_cuandoNoHayAsignacion() {
        // Arrange
        when(evaluacionAsesorPorEvaluacionFinder.obtener(evaluacion)).thenReturn(EvaluacionAsesorDomain.VACIO);

        // Act
        useCase.ejecutar(solicitud);

        // Assert
        var orden = inOrder(logger, contextoAsignacionEvaluacionFinder, asesorVigenteExisteFinder,
                evaluacionAsesorPorEvaluacionFinder, evaluacionAsesorConRegistrosFinder, validator,
                evaluacionAsesorOutputPort);
        orden.verify(logger).info(EvaluacionAsesorKey.LOG_ASIGNANDO, evaluacion, asesorNuevo);
        orden.verify(contextoAsignacionEvaluacionFinder).obtener(evaluacion);
        orden.verify(asesorVigenteExisteFinder).obtener(asesorNuevo);
        orden.verify(evaluacionAsesorPorEvaluacionFinder).obtener(evaluacion);
        orden.verify(evaluacionAsesorConRegistrosFinder).obtener(evaluacion);
        orden.verify(logger).debug(EvaluacionAsesorKey.LOG_VERIFICACION_ASIGNAR, evaluacion, true, true, false, false);
        orden.verify(validator).validar(
                new ExistenciaEvaluacion(evaluacion, true),
                new ExistenciaAsesor(asesorNuevo, true),
                new EstadoAsignacionEvaluacion(EstadoEvaluacion.PENDIENTE),
                new CambioAsesorEvaluacion(evaluacion, UtilUUID.obtenerUUIDPorDefecto(), asesorNuevo, false));
        var captor = ArgumentCaptor.forClass(EvaluacionAsesorEntity.class);
        orden.verify(evaluacionAsesorOutputPort).registrar(captor.capture());
        orden.verify(logger).info(EvaluacionAsesorKey.LOG_ASIGNADA, evaluacion, asesorNuevo);
        assertThat(captor.getValue().id()).isEqualTo(solicitud.getId());
        assertThat(captor.getValue().evaluacion()).isEqualTo(evaluacion);
        assertThat(captor.getValue().asesor()).isEqualTo(asesorNuevo);
        verify(evaluacionAsesorOutputPort, never()).actualizarAsesor(any(), any());
        verify(contextoAsignacionEvaluacionFinder, times(1)).obtener(any());
        verify(asesorVigenteExisteFinder, times(1)).obtener(any());
        verify(evaluacionAsesorPorEvaluacionFinder, times(1)).obtener(any());
        verify(evaluacionAsesorConRegistrosFinder, times(1)).obtener(any());
    }

    @Test
    void debeActualizarAsesor_cuandoAsesorDistinto() {
        // Arrange
        var actual = EvaluacionAsesorDomain.crear(evaluacion, UtilUUID.generarNuevoUUID());
        when(evaluacionAsesorPorEvaluacionFinder.obtener(evaluacion)).thenReturn(actual);

        // Act
        useCase.ejecutar(solicitud);

        // Assert
        var orden = inOrder(validator, evaluacionAsesorOutputPort, logger);
        orden.verify(validator).validar(any(), any(), any(), any());
        orden.verify(evaluacionAsesorOutputPort).actualizarAsesor(actual.getId(), asesorNuevo);
        orden.verify(logger).info(EvaluacionAsesorKey.LOG_ASIGNADA, evaluacion, asesorNuevo);
        verify(evaluacionAsesorOutputPort, never()).registrar(any());
    }

    @Test
    void debeSalirSinEscribir_cuandoAsesorIgual() {
        // Arrange
        var actual = EvaluacionAsesorDomain.crear(evaluacion, asesorNuevo);
        when(evaluacionAsesorPorEvaluacionFinder.obtener(evaluacion)).thenReturn(actual);

        // Act
        useCase.ejecutar(solicitud);

        // Assert
        verify(validator).validar(any(), any(), any(), any());
        verify(logger).info(EvaluacionAsesorKey.LOG_SIN_CAMBIOS, evaluacion, asesorNuevo);
        verify(evaluacionAsesorOutputPort, never()).registrar(any());
        verify(evaluacionAsesorOutputPort, never()).actualizarAsesor(any(), any());
        verify(logger, never()).info(eq(EvaluacionAsesorKey.LOG_ASIGNADA), any(), any());
    }

    @Test
    void debeRechazarSinEscribir_cuandoElValidatorLanza() {
        // Arrange
        when(evaluacionAsesorPorEvaluacionFinder.obtener(evaluacion)).thenReturn(EvaluacionAsesorDomain.VACIO);
        doThrow(new AsesorNoEncontradoException(asesorNuevo)).when(validator).validar(any(), any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(solicitud)).isInstanceOf(AsesorNoEncontradoException.class);
        verify(evaluacionAsesorOutputPort, never()).registrar(any());
        verify(evaluacionAsesorOutputPort, never()).actualizarAsesor(any(), any());
        verify(logger, never()).info(eq(EvaluacionAsesorKey.LOG_ASIGNADA), any(), any());
    }
}
