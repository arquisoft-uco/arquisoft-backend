package com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.application.evaluacion.query.usecase.ConsultarEvaluacionesCoordinadorUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEvaluacionesCoordinadorInteractorImplTest {

    @Mock
    private ConsultarEvaluacionesCoordinadorUseCase consultarEvaluacionesCoordinadorUseCase;

    @InjectMocks
    private ConsultarEvaluacionesCoordinadorInteractorImpl interactor;

    @Test
    void debeMapearLaQueryACriteria_yDelegarEnElUseCase() {
        // Arrange
        var query = ConsultaCriteriaQuery.crear(1, 5, List.of(), null);
        PaginatedResult<EvaluacionReadModel> esperado = PaginatedResult.of(List.of(), 1, 5, 0);
        when(consultarEvaluacionesCoordinadorUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);

        var captor = ArgumentCaptor.forClass(EvaluacionCriteria.class);
        verify(consultarEvaluacionesCoordinadorUseCase).ejecutar(captor.capture());
        var criteria = captor.getValue();
        assertThat(criteria.getPagina()).isEqualTo(1);
        assertThat(criteria.getTamanio()).isEqualTo(5);
        assertThat(criteria.getOrdenamiento().get(0).getCampo())
                .isEqualTo(EvaluacionCriteria.Campo.PROYECTO.getClave());
    }
}
