package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.readmodel.CriterioItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.AsesorDeEvaluacionAsesorQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.criteria.EvaluacionCualitativaAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel.EvaluacionCualitativaAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.secondaryport.EvaluacionCualitativaAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.validator.ConsultarEvaluacionesCualitativasAsesorValidator;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.readmodel.ItemCualitativoAsesorReadModel;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEvaluacionesCualitativasAsesorUseCaseImplTest {

    @Mock
    private AsesorDeEvaluacionAsesorQueryFinder asesorDeEvaluacionAsesorQueryFinder;

    @Mock
    private ConsultarEvaluacionesCualitativasAsesorValidator validator;

    @Mock
    private EvaluacionCualitativaAsesorQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEvaluacionesCualitativasAsesorUseCaseImpl useCase;

    private static EvaluacionCualitativaAsesorReadModel readModel(String item, String criterio) {
        return new EvaluacionCualitativaAsesorReadModel(
                UtilUUID.generarNuevoUUID(),
                new ItemCualitativoAsesorReadModel(UtilUUID.generarNuevoUUID(), item, "desc"),
                new CriterioItemCualitativoAsesorReadModel(UtilUUID.generarNuevoUUID(), criterio, "desc"));
    }

    @Test
    void debeConsultarEnOrden_cuandoElAccesoEsValido() {
        // Arrange
        var criteria = new EvaluacionCualitativaAsesorCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var esperado = List.of(
                readModel("Claridad", "Excelente"),
                readModel("Rigor", "Bueno"),
                readModel("Tono", "Regular"));
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
        verify(logger).debug(eq(EvaluacionCualitativaAsesorKey.LOG_CONSULTANDO), eq(criteria.evaluacionAsesor()));
        verify(logger).debug(eq(EvaluacionCualitativaAsesorKey.LOG_CONSULTA_COMPLETADA), eq(3));
    }

    @Test
    void debeRetornarListaVacia_cuandoLaEvaluacionNoTieneCualitativas() {
        // Arrange
        var criteria = new EvaluacionCualitativaAsesorCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        when(asesorDeEvaluacionAsesorQueryFinder.obtener(criteria.evaluacionAsesor())).thenReturn(criteria.asesor());
        when(queryOutputPort.consultar(criteria)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
