package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.criteria.EvaluacionCuantitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.model.ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.readmodel.EvaluacionCuantitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.usecase.ConsultarEvaluacionesCuantitativasJuradoUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEvaluacionesCuantitativasJuradoInteractorImplTest {

    @Mock
    private ConsultarEvaluacionesCuantitativasJuradoUseCase useCase;

    @InjectMocks
    private ConsultarEvaluacionesCuantitativasJuradoInteractorImpl interactor;

    @Test
    void debeMapearQueryACriteriaYDelegarEnElUseCase_retornandoElMismoResultado() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var query = ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery.crear(evaluacionJurado);
        List<EvaluacionCuantitativaJuradoReadModel> resultado = List.of();
        when(useCase.ejecutar(new EvaluacionCuantitativaJuradoCriteria(evaluacionJurado)))
                .thenReturn(resultado);

        // Act
        var respuesta = interactor.ejecutar(query);

        // Assert
        assertThat(respuesta).isSameAs(resultado);
        var captor = ArgumentCaptor.forClass(EvaluacionCuantitativaJuradoCriteria.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().evaluacionJuradoId()).isEqualTo(evaluacionJurado);
    }
}
