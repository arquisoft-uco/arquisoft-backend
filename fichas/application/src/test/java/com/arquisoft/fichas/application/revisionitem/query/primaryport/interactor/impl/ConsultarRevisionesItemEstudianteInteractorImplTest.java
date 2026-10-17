package com.arquisoft.fichas.application.revisionitem.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.revisionitem.query.criteria.RevisionItemEstudianteCriteria;
import com.arquisoft.fichas.application.revisionitem.query.primaryport.model.ConsultarRevisionesItemEstudianteQuery;
import com.arquisoft.fichas.application.revisionitem.query.readmodel.RevisionItemReadModel;
import com.arquisoft.fichas.application.revisionitem.query.usecase.ConsultarRevisionesItemEstudianteUseCase;
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
class ConsultarRevisionesItemEstudianteInteractorImplTest {

    @Mock
    private ConsultarRevisionesItemEstudianteUseCase consultarRevisionesItemEstudianteUseCase;

    @Captor
    private ArgumentCaptor<RevisionItemEstudianteCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarRevisionesItemEstudianteInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCaseConElCriteriaDelMapper_yRetornarSuResultado() {
        // Arrange
        var estudiante = UUID.randomUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarRevisionesItemEstudianteQuery.crear(estudiante, criterio);

        PaginatedResult<RevisionItemReadModel> resultadoEsperado = PaginatedResult.of(List.of(), 0, 10, 0L);
        when(consultarRevisionesItemEstudianteUseCase.ejecutar(any(RevisionItemEstudianteCriteria.class)))
                .thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(consultarRevisionesItemEstudianteUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().getRaiz()).isEqualTo(
                NodoFiltro.predicado("estudianteId", FiltroOperador.ES, estudiante.toString()));
    }
}
