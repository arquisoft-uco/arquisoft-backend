package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.criteria.EvaluacionCualitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.model.ConsultarEvaluacionesCualitativasJuradoEstudianteQuery;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.readmodel.EvaluacionCualitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.usecase.ConsultarEvaluacionesCualitativasJuradoUseCase;
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
class ConsultarEvaluacionesCualitativasJuradoInteractorImplTest {

    @Mock
    private ConsultarEvaluacionesCualitativasJuradoUseCase useCase;

    @InjectMocks
    private ConsultarEvaluacionesCualitativasJuradoInteractorImpl interactor;

    @Test
    void debeMapearQueryACriteriaYDelegarEnElUseCase_retornandoElMismoResultado() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        ConsultarEvaluacionesCualitativasJuradoEstudianteQuery query =
                ConsultarEvaluacionesCualitativasJuradoEstudianteQuery.crear(
                        evaluacionJurado, estudiante.toString());
        List<EvaluacionCualitativaJuradoReadModel> resultado = List.of();
        when(useCase.ejecutar(new EvaluacionCualitativaJuradoCriteria(evaluacionJurado, estudiante)))
                .thenReturn(resultado);

        // Act
        List<EvaluacionCualitativaJuradoReadModel> respuesta = interactor.ejecutar(query);

        // Assert
        assertThat(respuesta).isSameAs(resultado);
        ArgumentCaptor<EvaluacionCualitativaJuradoCriteria> captor =
                ArgumentCaptor.forClass(EvaluacionCualitativaJuradoCriteria.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().evaluacionJuradoId()).isEqualTo(evaluacionJurado);
        assertThat(captor.getValue().estudianteId()).isEqualTo(estudiante);
    }
}
