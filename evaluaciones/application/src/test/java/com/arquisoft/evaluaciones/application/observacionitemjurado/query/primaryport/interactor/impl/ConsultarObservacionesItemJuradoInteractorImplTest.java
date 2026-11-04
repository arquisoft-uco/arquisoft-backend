package com.arquisoft.evaluaciones.application.observacionitemjurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.observacionitemjurado.query.criteria.ObservacionItemJuradoCriteria;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.primaryport.model.ConsultarObservacionesItemJuradoQuery;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.readmodel.ObservacionItemJuradoReadModel;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.usecase.ConsultarObservacionesItemJuradoUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarObservacionesItemJuradoInteractorImplTest {

    @Mock
    private ConsultarObservacionesItemJuradoUseCase useCase;

    @InjectMocks
    private ConsultarObservacionesItemJuradoInteractorImpl interactor;

    @Test
    void debeMapearQueryACriteriaYDelegarEnElUseCase_retornandoLaMismaPagina() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var query = ConsultarObservacionesItemJuradoQuery.crear(
                evaluacionCuantitativaJurado, ConsultaCriteriaQuery.crear(1, 20, List.of(), null));
        PaginatedResult<ObservacionItemJuradoReadModel> pagina = PaginatedResult.of(List.of(), 1, 20, 0);
        when(useCase.ejecutar(any(ObservacionItemJuradoCriteria.class))).thenReturn(pagina);

        // Act
        var respuesta = interactor.ejecutar(query);

        // Assert
        assertThat(respuesta).isSameAs(pagina);
        var captor = ArgumentCaptor.forClass(ObservacionItemJuradoCriteria.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getEvaluacionCuantitativaJurado()).isEqualTo(evaluacionCuantitativaJurado);
        assertThat(captor.getValue().getPagina()).isEqualTo(1);
        assertThat(captor.getValue().getTamanio()).isEqualTo(20);
    }
}
