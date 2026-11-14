package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.EvaluacionCualitativaJuradoPorIdFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.EvaluacionCualitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.entity.EvaluacionCualitativaJuradoEntity;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.validator.CambiarCriterioEvaluacionCualitativaJuradoValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.EvaluacionJuradoEstadoFinder;
import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.command.finder.CriteriosCualitativosJuradoExistentesFinder;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.CambioCriterioEvaluacionCualitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.EvaluacionCualitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.CambioCriterioEvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.EstadoEvaluacionJuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class CambiarCriterioEvaluacionCualitativaJuradoUseCaseImplTest {

    @Mock
    private EvaluacionCualitativaJuradoPorIdFinder evaluacionCualitativaJuradoPorIdFinder;

    @Mock
    private EvaluacionJuradoEstadoFinder evaluacionJuradoEstadoFinder;

    @Mock
    private CriteriosCualitativosJuradoExistentesFinder criteriosCualitativosJuradoExistentesFinder;

    @Mock
    private CambiarCriterioEvaluacionCualitativaJuradoValidator validator;

    @Mock
    private EvaluacionCualitativaJuradoOutputPort evaluacionCualitativaJuradoOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private CambiarCriterioEvaluacionCualitativaJuradoUseCaseImpl useCase;

    @Test
    void debeActualizar_cuandoValidacionPasa() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var criterioAnterior = UtilUUID.generarNuevoUUID();
        var criterioNuevo = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var cambio = CambioCriterioEvaluacionCualitativaJuradoDomain.crear(id, jurado, criterioNuevo);
        var evaluacion = EvaluacionCualitativaJuradoDomain.reconstruir(id, evaluacionJurado, item, criterioAnterior);
        var estado = EstadoEvaluacionJuradoDomain.reconstruir(evaluacionJurado, jurado, EstadoEvaluacion.EN_PROGRESO);
        var existentes = Set.of(criterioNuevo);
        when(evaluacionCualitativaJuradoPorIdFinder.obtener(id)).thenReturn(evaluacion);
        when(evaluacionJuradoEstadoFinder.obtener(evaluacionJurado)).thenReturn(estado);
        when(criteriosCualitativosJuradoExistentesFinder.obtener(Set.of(criterioNuevo))).thenReturn(existentes);

        // Act
        useCase.ejecutar(cambio);

        // Assert
        var orden = inOrder(evaluacionCualitativaJuradoPorIdFinder, evaluacionJuradoEstadoFinder,
                criteriosCualitativosJuradoExistentesFinder, validator,
                evaluacionCualitativaJuradoOutputPort);
        orden.verify(evaluacionCualitativaJuradoPorIdFinder).obtener(id);
        orden.verify(evaluacionJuradoEstadoFinder).obtener(evaluacionJurado);
        orden.verify(criteriosCualitativosJuradoExistentesFinder).obtener(Set.of(criterioNuevo));
        orden.verify(validator).validar(cambio, evaluacion, estado, existentes);
        orden.verify(evaluacionCualitativaJuradoOutputPort).actualizar(any(EvaluacionCualitativaJuradoEntity.class));

        verify(evaluacionCualitativaJuradoPorIdFinder, times(1)).obtener(any());
        verify(evaluacionJuradoEstadoFinder, times(1)).obtener(any());
        verify(criteriosCualitativosJuradoExistentesFinder, times(1)).obtener(any());

        var entidad = ArgumentCaptor.forClass(EvaluacionCualitativaJuradoEntity.class);
        verify(evaluacionCualitativaJuradoOutputPort).actualizar(entidad.capture());
        assertThat(entidad.getValue())
                .isEqualTo(new EvaluacionCualitativaJuradoEntity(id, evaluacionJurado, item, criterioNuevo));

        verify(logger).info(any(ClaveMensaje.class), eq(id), eq(criterioNuevo));
        verify(logger).debug(any(ClaveMensaje.class), eq(true), eq(jurado), eq("EN_PROGRESO"), eq(1));
        verify(logger).info(any(ClaveMensaje.class), eq(id));
    }

    @Test
    void debeNoPersistir_cuandoElValidatorLanza() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var cambio = CambioCriterioEvaluacionCualitativaJuradoDomain.crear(id, jurado, criterio);
        var evaluacion = EvaluacionCualitativaJuradoDomain.reconstruir(
                id, evaluacionJurado, UtilUUID.generarNuevoUUID(), criterio);
        var estado = EstadoEvaluacionJuradoDomain.reconstruir(evaluacionJurado, jurado, EstadoEvaluacion.PENDIENTE);
        when(evaluacionCualitativaJuradoPorIdFinder.obtener(id)).thenReturn(evaluacion);
        when(evaluacionJuradoEstadoFinder.obtener(evaluacionJurado)).thenReturn(estado);
        when(criteriosCualitativosJuradoExistentesFinder.obtener(Set.of(criterio))).thenReturn(Set.of(criterio));
        doThrow(new CambioCriterioEvaluacionJuradoFinalizadaException(evaluacionJurado))
                .when(validator).validar(any(), any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(cambio))
                .isInstanceOf(CambioCriterioEvaluacionJuradoFinalizadaException.class);
        verify(evaluacionCualitativaJuradoOutputPort, never()).actualizar(any());
    }
}
