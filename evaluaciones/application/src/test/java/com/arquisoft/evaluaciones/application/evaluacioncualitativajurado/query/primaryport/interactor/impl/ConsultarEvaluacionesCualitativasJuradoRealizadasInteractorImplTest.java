package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.criteria.EvaluacionCualitativaJuradoRealizadaCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.model.ConsultarEvaluacionesCualitativasJuradoRealizadasQuery;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.readmodel.EvaluacionCualitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.usecase.ConsultarEvaluacionesCualitativasJuradoRealizadasUseCase;
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
class ConsultarEvaluacionesCualitativasJuradoRealizadasInteractorImplTest {

    @Mock
    private ConsultarEvaluacionesCualitativasJuradoRealizadasUseCase useCase;

    @InjectMocks
    private ConsultarEvaluacionesCualitativasJuradoRealizadasInteractorImpl interactor;

    @Test
    void debeDelegarAlUseCaseConElCriteria() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesCualitativasJuradoRealizadasQuery.crear(evaluacionJurado, jurado);
        var criteria = new EvaluacionCualitativaJuradoRealizadaCriteria(evaluacionJurado, jurado);
        List<EvaluacionCualitativaJuradoReadModel> esperado = List.of();
        when(useCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteria);
    }
}
