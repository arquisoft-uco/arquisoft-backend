package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.command.finder.CriteriosCualitativosAsesorExistentesFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.ContextoRegistroEvaluacionAsesorPorEvaluacionCualitativaFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.finder.EvaluacionCualitativaAsesorPorIdFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.EvaluacionCualitativaAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.entity.EvaluacionCualitativaAsesorEntity;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.validator.CambiarCriterioEvaluacionCualitativaAsesorValidator;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.ContextoRegistroEvaluacionAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.CambioCriterioEvaluacionCualitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.EvaluacionCualitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.CambioCriterioEvaluacionCualitativaAsesorFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.EstadoCambioCriterioEvaluacionCualitativaAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaCriteriosCualitativosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionCualitativaAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaAsesorKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

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
class CambiarCriterioEvaluacionCualitativaAsesorUseCaseImplTest {

    @Mock
    private ContextoRegistroEvaluacionAsesorPorEvaluacionCualitativaFinder contextoFinder;

    @Mock
    private EvaluacionCualitativaAsesorPorIdFinder evaluacionCualitativaAsesorPorIdFinder;

    @Mock
    private CriteriosCualitativosAsesorExistentesFinder criteriosCualitativosAsesorExistentesFinder;

    @Mock
    private CambiarCriterioEvaluacionCualitativaAsesorValidator validator;

    @Mock
    private EvaluacionCualitativaAsesorOutputPort evaluacionCualitativaAsesorOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private CambiarCriterioEvaluacionCualitativaAsesorUseCaseImpl useCase;

    @Test
    void debeActualizarCriterio_cuandoEsDistinto() {
        // Arrange
        var evaluacionCualitativa = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var criterioActual = UtilUUID.generarNuevoUUID();
        var criterioNuevo = UtilUUID.generarNuevoUUID();
        var cambio = CambioCriterioEvaluacionCualitativaAsesorDomain.crear(evaluacionCualitativa, asesor, criterioNuevo);
        var contexto = contexto(evaluacionAsesor, asesor, EstadoEvaluacion.EN_PROGRESO);
        var evaluacion = EvaluacionCualitativaAsesorDomain.reconstruir(
                evaluacionCualitativa, evaluacionAsesor, item, criterioActual);
        when(contextoFinder.obtener(evaluacionCualitativa)).thenReturn(contexto);
        when(evaluacionCualitativaAsesorPorIdFinder.obtener(evaluacionCualitativa)).thenReturn(evaluacion);
        when(criteriosCualitativosAsesorExistentesFinder.obtener(Set.of(criterioNuevo)))
                .thenReturn(Set.of(criterioNuevo));

        // Act
        useCase.ejecutar(cambio);

        // Assert
        var orden = inOrder(logger, contextoFinder, evaluacionCualitativaAsesorPorIdFinder,
                criteriosCualitativosAsesorExistentesFinder, validator, evaluacionCualitativaAsesorOutputPort);
        orden.verify(logger).info(EvaluacionCualitativaAsesorKey.LOG_CAMBIANDO_CRITERIO,
                evaluacionCualitativa, criterioNuevo);
        orden.verify(contextoFinder).obtener(evaluacionCualitativa);
        orden.verify(evaluacionCualitativaAsesorPorIdFinder).obtener(evaluacionCualitativa);
        orden.verify(criteriosCualitativosAsesorExistentesFinder).obtener(Set.of(criterioNuevo));
        orden.verify(logger).debug(EvaluacionCualitativaAsesorKey.LOG_VERIFICACION_CAMBIO_CRITERIO,
                true, true, EstadoEvaluacion.EN_PROGRESO.getId(), 1);
        orden.verify(validator).validar(
                new ExistenciaEvaluacionCualitativaAsesor(evaluacionCualitativa, true, true),
                new PropiedadEvaluacionAsesor(evaluacionAsesor, asesor, asesor),
                new EstadoCambioCriterioEvaluacionCualitativaAsesor(evaluacionAsesor, EstadoEvaluacion.EN_PROGRESO),
                new ExistenciaCriteriosCualitativosAsesor(Set.of(criterioNuevo), Set.of(criterioNuevo)));
        orden.verify(evaluacionCualitativaAsesorOutputPort).actualizar(
                new EvaluacionCualitativaAsesorEntity(evaluacionCualitativa, evaluacionAsesor, item, criterioNuevo));
        orden.verify(logger).info(EvaluacionCualitativaAsesorKey.LOG_CRITERIO_CAMBIADO, evaluacionCualitativa);
        verify(contextoFinder, times(1)).obtener(any());
        verify(evaluacionCualitativaAsesorPorIdFinder, times(1)).obtener(any());
        verify(criteriosCualitativosAsesorExistentesFinder, times(1)).obtener(any());
    }

    @Test
    void debeOmitirActualizar_cuandoCriterioEsElMismo() {
        // Arrange
        var evaluacionCualitativa = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();
        var cambio = CambioCriterioEvaluacionCualitativaAsesorDomain.crear(evaluacionCualitativa, asesor, criterio);
        var evaluacion = EvaluacionCualitativaAsesorDomain.reconstruir(
                evaluacionCualitativa, evaluacionAsesor, UtilUUID.generarNuevoUUID(), criterio);
        when(contextoFinder.obtener(evaluacionCualitativa))
                .thenReturn(contexto(evaluacionAsesor, asesor, EstadoEvaluacion.PENDIENTE));
        when(evaluacionCualitativaAsesorPorIdFinder.obtener(evaluacionCualitativa)).thenReturn(evaluacion);
        when(criteriosCualitativosAsesorExistentesFinder.obtener(Set.of(criterio))).thenReturn(Set.of(criterio));

        // Act
        useCase.ejecutar(cambio);

        // Assert
        verify(validator).validar(any(), any(), any(), any());
        verify(evaluacionCualitativaAsesorOutputPort, never()).actualizar(any());
        verify(logger).info(EvaluacionCualitativaAsesorKey.LOG_CRITERIO_CAMBIADO, evaluacionCualitativa);
    }

    @Test
    void debeNoActualizar_cuandoValidatorRechaza() {
        // Arrange
        var evaluacionCualitativa = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var criterioNuevo = UtilUUID.generarNuevoUUID();
        var cambio = CambioCriterioEvaluacionCualitativaAsesorDomain.crear(evaluacionCualitativa, asesor, criterioNuevo);
        var evaluacion = EvaluacionCualitativaAsesorDomain.reconstruir(
                evaluacionCualitativa, evaluacionAsesor, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        when(contextoFinder.obtener(evaluacionCualitativa))
                .thenReturn(contexto(evaluacionAsesor, asesor, EstadoEvaluacion.FINALIZADA));
        when(evaluacionCualitativaAsesorPorIdFinder.obtener(evaluacionCualitativa)).thenReturn(evaluacion);
        when(criteriosCualitativosAsesorExistentesFinder.obtener(Set.of(criterioNuevo)))
                .thenReturn(Set.of(criterioNuevo));
        doThrow(new CambioCriterioEvaluacionCualitativaAsesorFinalizadaException(evaluacionAsesor))
                .when(validator).validar(any(), any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(cambio))
                .isInstanceOf(CambioCriterioEvaluacionCualitativaAsesorFinalizadaException.class);
        verify(evaluacionCualitativaAsesorOutputPort, never()).actualizar(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(evaluacionCualitativa));
    }

    private static ContextoRegistroEvaluacionAsesorDomain contexto(
            UUID evaluacionAsesor, UUID asesor, EstadoEvaluacion estado) {
        return ContextoRegistroEvaluacionAsesorDomain.reconstruir(
                evaluacionAsesor, UtilUUID.generarNuevoUUID(), estado, UtilUUID.generarNuevoUUID(), asesor);
    }
}
