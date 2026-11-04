package com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionesAsignadasJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model.ConsultarEvaluacionesAsignadasJuradoQuery;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionAsignadaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.usecase.ConsultarEvaluacionesAsignadasJuradoUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
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
class ConsultarEvaluacionesAsignadasJuradoInteractorImplTest {

    @Mock
    private ConsultarEvaluacionesAsignadasJuradoUseCase useCase;

    @InjectMocks
    private ConsultarEvaluacionesAsignadasJuradoInteractorImpl interactor;

    @Test
    void debeMapearQueryACriteriaYDelegar_retornandoElMismoResultado() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesAsignadasJuradoQuery.crear(
                jurado, ConsultaCriteriaQuery.crear(2, 5, List.of(), null));
        var esperado = PaginatedResult.<EvaluacionAsignadaJuradoReadModel>of(List.of(), 2, 5, 0);
        when(useCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(EvaluacionesAsignadasJuradoCriteria.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getJurado()).isEqualTo(jurado);
        assertThat(captor.getValue().getPagina()).isEqualTo(2);
        assertThat(captor.getValue().getTamanio()).isEqualTo(5);
    }
}
