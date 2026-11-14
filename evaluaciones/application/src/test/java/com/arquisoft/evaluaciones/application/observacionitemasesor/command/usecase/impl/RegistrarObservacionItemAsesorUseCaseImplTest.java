package com.arquisoft.evaluaciones.application.observacionitemasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.ContextoEvaluacionCuantitativaAsesorFinder;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.finder.DescripcionObservacionItemAsesorExisteFinder;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.finder.model.CriterioDescripcionObservacionItemAsesor;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.secondaryport.ObservacionItemAsesorOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.secondaryport.entity.ObservacionItemAsesorEntity;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.validator.RegistrarObservacionItemAsesorValidator;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.ContextoEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.ObservacionItemAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.RegistroObservacionItemAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.EvaluacionCuantitativaAsesorNoEncontradaException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemAsesorKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class RegistrarObservacionItemAsesorUseCaseImplTest {

    @Mock
    private ObservacionItemAsesorOutputPort outputPort;

    @Mock
    private ContextoEvaluacionCuantitativaAsesorFinder contextoFinder;

    @Mock
    private DescripcionObservacionItemAsesorExisteFinder descripcionExisteFinder;

    @Mock
    private RegistrarObservacionItemAsesorValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RegistrarObservacionItemAsesorUseCaseImpl useCase;

    @Test
    void debeRegistrar_cuandoValidacionPasa() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var observacion = ObservacionItemAsesorDomain.crear(evaluacion, "Sustento");
        var registro = RegistroObservacionItemAsesorDomain.crear(observacion, asesor);
        var contexto = ContextoEvaluacionCuantitativaAsesorDomain.reconstruir(
                evaluacion, UtilUUID.generarNuevoUUID(), asesor, EstadoEvaluacion.EN_PROGRESO);
        var criterio = new CriterioDescripcionObservacionItemAsesor(evaluacion, "Sustento");
        when(contextoFinder.obtener(evaluacion)).thenReturn(contexto);
        when(descripcionExisteFinder.obtener(criterio)).thenReturn(false);

        // Act
        var resultado = useCase.ejecutar(registro);

        // Assert
        assertThat(resultado).isEqualTo(observacion.getId());
        var orden = inOrder(logger, contextoFinder, descripcionExisteFinder, validator, outputPort);
        orden.verify(logger).info(ObservacionItemAsesorKey.LOG_REGISTRANDO, evaluacion);
        orden.verify(contextoFinder).obtener(evaluacion);
        orden.verify(descripcionExisteFinder).obtener(criterio);
        orden.verify(logger).debug(ObservacionItemAsesorKey.LOG_VERIFICACION_REGISTRAR, true, false);
        orden.verify(validator).validar(registro, contexto, false);
        orden.verify(outputPort).registrar(new ObservacionItemAsesorEntity(observacion.getId(), evaluacion, "Sustento"));
        orden.verify(logger).info(ObservacionItemAsesorKey.LOG_REGISTRADO, observacion.getId());
        verify(contextoFinder, times(1)).obtener(any());
        verify(descripcionExisteFinder, times(1)).obtener(any());
    }

    @Test
    void debePropagarExcepcion_cuandoValidatorRechaza() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var observacion = ObservacionItemAsesorDomain.crear(evaluacion, "Sustento");
        var registro = RegistroObservacionItemAsesorDomain.crear(observacion, UtilUUID.generarNuevoUUID());
        when(contextoFinder.obtener(evaluacion)).thenReturn(ContextoEvaluacionCuantitativaAsesorDomain.VACIO);
        when(descripcionExisteFinder.obtener(any())).thenReturn(false);
        doThrow(new EvaluacionCuantitativaAsesorNoEncontradaException(evaluacion))
                .when(validator).validar(eq(registro), any(), eq(false));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(registro))
                .isInstanceOf(EvaluacionCuantitativaAsesorNoEncontradaException.class);
        verify(outputPort, never()).registrar(any());
        verify(logger, never()).info(any(ObservacionItemAsesorKey.class), eq(observacion.getId()));
    }
}
