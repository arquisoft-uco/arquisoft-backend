package com.arquisoft.evaluaciones.application.evaluacionasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionAsesorRealizadaCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.AsesorDeEvaluacionAsesorQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorRealizadaReadModel;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.secondaryport.EvaluacionAsesorRealizadaQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.validator.ConsultarEvaluacionAsesorRealizadaValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoPerteneceAsesorException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionAsesorKey;
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
class ConsultarEvaluacionAsesorRealizadaUseCaseImplTest {

    @Mock
    private AsesorDeEvaluacionAsesorQueryFinder asesorDeEvaluacionAsesorQueryFinder;

    @Mock
    private ConsultarEvaluacionAsesorRealizadaValidator validator;

    @Mock
    private EvaluacionAsesorRealizadaQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEvaluacionAsesorRealizadaUseCaseImpl useCase;

    @Test
    void debeConsultar_cuandoLaValidacionPasa() {
        // Arrange
        var criteria = new EvaluacionAsesorRealizadaCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var esperado = new EvaluacionAsesorRealizadaReadModel(criteria.evaluacionAsesor(), null, null);
        when(asesorDeEvaluacionAsesorQueryFinder.obtener(criteria.evaluacionAsesor())).thenReturn(criteria.asesor());
        when(queryOutputPort.consultar(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var orden = inOrder(asesorDeEvaluacionAsesorQueryFinder, validator, queryOutputPort);
        orden.verify(asesorDeEvaluacionAsesorQueryFinder).obtener(criteria.evaluacionAsesor());
        orden.verify(validator).validar(criteria.evaluacionAsesor(), criteria.asesor(), criteria.asesor());
        orden.verify(queryOutputPort).consultar(criteria);
        verify(asesorDeEvaluacionAsesorQueryFinder, times(1)).obtener(any());
        verify(queryOutputPort, times(1)).consultar(any());
        verify(logger).debug(eq(EvaluacionAsesorKey.LOG_CONSULTANDO_REALIZADA), eq(criteria.evaluacionAsesor()));
        verify(logger).debug(eq(EvaluacionAsesorKey.LOG_CONSULTA_REALIZADA_COMPLETADA), eq(esperado.id()));
    }

    @Test
    void debePropagarElRechazo_cuandoElValidatorLanza() {
        // Arrange
        var criteria = new EvaluacionAsesorRealizadaCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var asignado = UtilUUID.generarNuevoUUID();
        when(asesorDeEvaluacionAsesorQueryFinder.obtener(criteria.evaluacionAsesor())).thenReturn(asignado);
        doThrow(new EvaluacionAsesorNoPerteneceAsesorException(criteria.evaluacionAsesor()))
                .when(validator).validar(criteria.evaluacionAsesor(), asignado, criteria.asesor());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(EvaluacionAsesorNoPerteneceAsesorException.class);
        verify(queryOutputPort, never()).consultar(any());
        verify(logger, never()).debug(eq(EvaluacionAsesorKey.LOG_CONSULTA_REALIZADA_COMPLETADA), any());
    }
}
