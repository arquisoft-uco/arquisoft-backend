package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.criteria.EvaluacionCualitativaAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.model.ConsultarEvaluacionesCualitativasAsesorQuery;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel.EvaluacionCualitativaAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.usecase.ConsultarEvaluacionesCualitativasAsesorUseCase;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEvaluacionesCualitativasAsesorInteractorImplTest {

    @Mock
    private ConsultarEvaluacionesCualitativasAsesorUseCase useCase;

    @InjectMocks
    private ConsultarEvaluacionesCualitativasAsesorInteractorImpl interactor;

    @Test
    void debeConvertirQueryEnCriteriaYDelegar_retornandoElMismoResultado() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesCualitativasAsesorQuery.crear(evaluacionAsesor, asesor);
        var criteria = new EvaluacionCualitativaAsesorCriteria(evaluacionAsesor, asesor);
        List<EvaluacionCualitativaAsesorReadModel> esperado = List.of();
        when(useCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteria);
    }
}
