package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.criteria.EvaluacionCuantitativaJuradoRealizadaCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.model.ConsultarEvaluacionesCuantitativasJuradoRealizadasQuery;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.readmodel.EvaluacionCuantitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.usecase.ConsultarEvaluacionesCuantitativasJuradoRealizadasUseCase;
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
class ConsultarEvaluacionesCuantitativasJuradoRealizadasInteractorImplTest {

    @Mock
    private ConsultarEvaluacionesCuantitativasJuradoRealizadasUseCase useCase;

    @InjectMocks
    private ConsultarEvaluacionesCuantitativasJuradoRealizadasInteractorImpl interactor;

    @Test
    void debeDelegarAlUseCaseConElCriteria_cuandoQueryValida() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesCuantitativasJuradoRealizadasQuery.crear(evaluacionJurado, jurado);
        var criteria = new EvaluacionCuantitativaJuradoRealizadaCriteria(evaluacionJurado, jurado);
        List<EvaluacionCuantitativaJuradoReadModel> esperado = List.of();
        when(useCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteria);
    }
}
