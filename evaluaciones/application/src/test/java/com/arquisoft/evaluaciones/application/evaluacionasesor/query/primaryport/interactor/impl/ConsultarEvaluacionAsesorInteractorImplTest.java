package com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionAsesorDeEvaluacionCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model.ConsultarEvaluacionAsesorQuery;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.usecase.ConsultarEvaluacionAsesorUseCase;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEvaluacionAsesorInteractorImplTest {

    @Mock
    private ConsultarEvaluacionAsesorUseCase useCase;

    @InjectMocks
    private ConsultarEvaluacionAsesorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElCriteria_cuandoSeEjecuta() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionAsesorQuery.crear(evaluacion);
        var criteria = new EvaluacionAsesorDeEvaluacionCriteria(evaluacion);
        var esperado = new EvaluacionAsesorReadModel(UtilUUID.generarNuevoUUID(),
                new EvaluacionAsesorReadModel.Asesor(UtilUUID.generarNuevoUUID(), "Ana", "ana@uco.edu.co"));
        when(useCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteria);
    }
}
