package com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionesAsignadasJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model.ConsultarEvaluacionesAsignadasJuradoQuery;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionAsignadaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.usecase.ConsultarEvaluacionesAsignadasJuradoUseCase;
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
class ConsultarEvaluacionesAsignadasJuradoInteractorImplTest {

    @Mock
    private ConsultarEvaluacionesAsignadasJuradoUseCase useCase;

    @InjectMocks
    private ConsultarEvaluacionesAsignadasJuradoInteractorImpl interactor;

    @Test
    void debeMapearQueryACriteriaYDelegar_retornandoElMismoResultado() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesAsignadasJuradoQuery.crear(jurado);
        var esperado = List.<EvaluacionAsignadaJuradoReadModel>of();
        when(useCase.ejecutar(new EvaluacionesAsignadasJuradoCriteria(jurado))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(new EvaluacionesAsignadasJuradoCriteria(jurado));
    }
}
