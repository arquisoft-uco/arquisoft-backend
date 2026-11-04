package com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionAsesorRealizadaCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model.ConsultarEvaluacionAsesorRealizadaQuery;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorRealizadaReadModel;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.usecase.ConsultarEvaluacionAsesorRealizadaUseCase;
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
class ConsultarEvaluacionAsesorRealizadaInteractorImplTest {

    @Mock
    private ConsultarEvaluacionAsesorRealizadaUseCase useCase;

    @InjectMocks
    private ConsultarEvaluacionAsesorRealizadaInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElCriteria_cuandoSeEjecuta() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionAsesorRealizadaQuery.crear(evaluacionAsesor, asesor);
        var criteria = new EvaluacionAsesorRealizadaCriteria(evaluacionAsesor, asesor);
        var esperado = new EvaluacionAsesorRealizadaReadModel(evaluacionAsesor, null, null);
        when(useCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteria);
    }
}
