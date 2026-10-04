package com.arquisoft.fichas.application.observacionitem.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemEstudianteCriteria;
import com.arquisoft.fichas.application.observacionitem.query.primaryport.model.ConsultarObservacionesItemEstudianteQuery;
import com.arquisoft.fichas.application.observacionitem.query.readmodel.ObservacionItemReadModel;
import com.arquisoft.fichas.application.observacionitem.query.usecase.ConsultarObservacionesItemEstudianteUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
class ConsultarObservacionesItemEstudianteInteractorImplTest {

    @Mock
    private ConsultarObservacionesItemEstudianteUseCase consultarObservacionesItemEstudianteUseCase;

    @Captor
    private ArgumentCaptor<ObservacionItemEstudianteCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarObservacionesItemEstudianteInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCaseConElCriteriaDelMapper_yRetornarSuResultado() {
        // Arrange
        var estudiante = UUID.randomUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarObservacionesItemEstudianteQuery.crear(estudiante, criterio);

        var resultadoEsperado = PaginatedResult.of(List.<ObservacionItemReadModel>of(), 0, 10, 0L);
        when(consultarObservacionesItemEstudianteUseCase.ejecutar(any(ObservacionItemEstudianteCriteria.class)))
                .thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(consultarObservacionesItemEstudianteUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().getRaiz()).isEqualTo(
                NodoFiltro.predicado("estudianteId", FiltroOperador.ES, estudiante.toString()));
    }
}
