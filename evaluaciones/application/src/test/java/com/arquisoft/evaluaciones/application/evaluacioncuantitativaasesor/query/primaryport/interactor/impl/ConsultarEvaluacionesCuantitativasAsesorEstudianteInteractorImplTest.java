package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.criteria.EvaluacionCuantitativaAsesorEstudianteCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.model.ConsultarEvaluacionesCuantitativasAsesorEstudianteQuery;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.readmodel.EvaluacionCuantitativaAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.usecase.ConsultarEvaluacionesCuantitativasAsesorEstudianteUseCase;
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
class ConsultarEvaluacionesCuantitativasAsesorEstudianteInteractorImplTest {

    @Mock
    private ConsultarEvaluacionesCuantitativasAsesorEstudianteUseCase useCase;

    @InjectMocks
    private ConsultarEvaluacionesCuantitativasAsesorEstudianteInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCase_conElCriteriaMapeado() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesCuantitativasAsesorEstudianteQuery.crear(evaluacionAsesor, estudiante);
        var criteria = new EvaluacionCuantitativaAsesorEstudianteCriteria(evaluacionAsesor, estudiante);
        List<EvaluacionCuantitativaAsesorReadModel> esperado = List.of();
        when(useCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteria);
    }
}
