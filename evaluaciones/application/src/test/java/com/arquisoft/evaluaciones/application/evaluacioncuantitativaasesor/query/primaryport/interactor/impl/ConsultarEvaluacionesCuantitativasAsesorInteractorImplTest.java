package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.criteria.EvaluacionCuantitativaAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.model.ConsultarEvaluacionesCuantitativasAsesorQuery;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.readmodel.EvaluacionCuantitativaAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.usecase.ConsultarEvaluacionesCuantitativasAsesorUseCase;
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
class ConsultarEvaluacionesCuantitativasAsesorInteractorImplTest {

    @Mock
    private ConsultarEvaluacionesCuantitativasAsesorUseCase useCase;

    @InjectMocks
    private ConsultarEvaluacionesCuantitativasAsesorInteractorImpl interactor;

    @Test
    void debeConvertirQueryEnCriteriaYDelegar_retornandoElMismoResultado() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesCuantitativasAsesorQuery.crear(evaluacionAsesor, asesor);
        var criteria = new EvaluacionCuantitativaAsesorCriteria(evaluacionAsesor, asesor);
        List<EvaluacionCuantitativaAsesorReadModel> esperado = List.of();
        when(useCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteria);
    }
}
