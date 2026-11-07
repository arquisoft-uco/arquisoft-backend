package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.criteria.EvaluacionCualitativaAsesorEstudianteCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.model.ConsultarEvaluacionesCualitativasAsesorEstudianteQuery;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel.EvaluacionCualitativaAsesorEstudianteReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.usecase.ConsultarEvaluacionesCualitativasAsesorEstudianteUseCase;
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
class ConsultarEvaluacionesCualitativasAsesorEstudianteInteractorImplTest {

    @Mock
    private ConsultarEvaluacionesCualitativasAsesorEstudianteUseCase useCase;

    @InjectMocks
    private ConsultarEvaluacionesCualitativasAsesorEstudianteInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCase_conElCriteriaMapeado() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesCualitativasAsesorEstudianteQuery.crear(evaluacionAsesor, estudiante);
        var criteria = new EvaluacionCualitativaAsesorEstudianteCriteria(evaluacionAsesor, estudiante);
        List<EvaluacionCualitativaAsesorEstudianteReadModel> esperado = List.of();
        when(useCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteria);
    }
}
