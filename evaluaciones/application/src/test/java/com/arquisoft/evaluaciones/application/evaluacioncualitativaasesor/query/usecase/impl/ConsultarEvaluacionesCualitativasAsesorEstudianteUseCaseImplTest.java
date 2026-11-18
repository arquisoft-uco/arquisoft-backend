package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.AsesorDeEvaluacionAsesorQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.EstudiantesDeEvaluacionAsesorQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.criteria.EvaluacionCualitativaAsesorEstudianteCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel.EvaluacionCualitativaAsesorEstudianteReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.secondaryport.EvaluacionCualitativaAsesorEstudianteQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.validator.ConsultarEvaluacionesCualitativasAsesorEstudianteValidator;
import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.readmodel.CriterioItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.readmodel.ItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.exception.EvaluacionAsesorNoPerteneceEstudianteException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaAsesorKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
class ConsultarEvaluacionesCualitativasAsesorEstudianteUseCaseImplTest {

    @Mock
    private AsesorDeEvaluacionAsesorQueryFinder asesorDeEvaluacionAsesorQueryFinder;

    @Mock
    private EstudiantesDeEvaluacionAsesorQueryFinder estudiantesDeEvaluacionAsesorQueryFinder;

    @Mock
    private ConsultarEvaluacionesCualitativasAsesorEstudianteValidator validator;

    @Mock
    private EvaluacionCualitativaAsesorEstudianteQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEvaluacionesCualitativasAsesorEstudianteUseCaseImpl useCase;

    private static EvaluacionCualitativaAsesorEstudianteReadModel readModel(String item) {
        return new EvaluacionCualitativaAsesorEstudianteReadModel(
                UtilUUID.generarNuevoUUID(),
                new ItemCualitativoAsesorReadModel(UtilUUID.generarNuevoUUID(), item, "desc"),
                new CriterioItemCualitativoAsesorReadModel(UtilUUID.generarNuevoUUID(), "Excelente", "desc"));
    }

    @Test
    void debeConsultarEnOrden_cuandoValidacionExitosa() {
        // Arrange
        var criteria = new EvaluacionCualitativaAsesorEstudianteCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var asesor = UtilUUID.generarNuevoUUID();
        var vinculados = List.of(criteria.estudiante(), UtilUUID.generarNuevoUUID());
        var esperado = List.of(readModel("Claridad"), readModel("Rigor"), readModel("Tono"));
        when(asesorDeEvaluacionAsesorQueryFinder.obtener(criteria.evaluacionAsesor())).thenReturn(asesor);
        when(estudiantesDeEvaluacionAsesorQueryFinder.obtener(criteria.evaluacionAsesor())).thenReturn(vinculados);
        when(queryOutputPort.consultarPorEvaluacionAsesor(criteria.evaluacionAsesor())).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var orden = inOrder(asesorDeEvaluacionAsesorQueryFinder, estudiantesDeEvaluacionAsesorQueryFinder,
                validator, queryOutputPort);
        orden.verify(asesorDeEvaluacionAsesorQueryFinder).obtener(criteria.evaluacionAsesor());
        orden.verify(estudiantesDeEvaluacionAsesorQueryFinder).obtener(criteria.evaluacionAsesor());
        orden.verify(validator).validar(criteria.evaluacionAsesor(), asesor, vinculados, criteria.estudiante());
        orden.verify(queryOutputPort).consultarPorEvaluacionAsesor(criteria.evaluacionAsesor());
        verify(asesorDeEvaluacionAsesorQueryFinder, times(1)).obtener(any());
        verify(estudiantesDeEvaluacionAsesorQueryFinder, times(1)).obtener(any());
        verify(queryOutputPort, times(1)).consultarPorEvaluacionAsesor(any());
        verify(logger).debug(eq(EvaluacionCualitativaAsesorKey.LOG_CONSULTANDO), eq(criteria.evaluacionAsesor()));
        verify(logger).debug(eq(EvaluacionCualitativaAsesorKey.LOG_CONSULTA_COMPLETADA), eq(3));
    }

    @Test
    void debeNoLeer_cuandoValidatorRechaza() {
        // Arrange
        var criteria = new EvaluacionCualitativaAsesorEstudianteCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        when(asesorDeEvaluacionAsesorQueryFinder.obtener(criteria.evaluacionAsesor()))
                .thenReturn(UtilUUID.generarNuevoUUID());
        when(estudiantesDeEvaluacionAsesorQueryFinder.obtener(criteria.evaluacionAsesor())).thenReturn(List.of());
        doThrow(new EvaluacionAsesorNoPerteneceEstudianteException(criteria.evaluacionAsesor(), criteria.estudiante()))
                .when(validator).validar(any(), any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(EvaluacionAsesorNoPerteneceEstudianteException.class);
        verify(queryOutputPort, never()).consultarPorEvaluacionAsesor(any());
        verify(logger, never()).debug(eq(EvaluacionCualitativaAsesorKey.LOG_CONSULTA_COMPLETADA), any());
    }
}
