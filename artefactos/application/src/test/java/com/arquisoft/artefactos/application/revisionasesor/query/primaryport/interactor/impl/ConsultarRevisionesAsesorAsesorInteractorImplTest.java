package com.arquisoft.artefactos.application.revisionasesor.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.revisionasesor.query.criteria.RevisionAsesorAsesorCriteria;
import com.arquisoft.artefactos.application.revisionasesor.query.primaryport.model.ConsultarRevisionesAsesorAsesorQuery;
import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorAsesorReadModel;
import com.arquisoft.artefactos.application.revisionasesor.query.usecase.ConsultarRevisionesAsesorAsesorUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarRevisionesAsesorAsesorInteractorImplTest {

    @Mock
    private ConsultarRevisionesAsesorAsesorUseCase consultarRevisionesAsesorAsesorUseCase;

    @Captor
    private ArgumentCaptor<RevisionAsesorAsesorCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarRevisionesAsesorAsesorInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCaseConElCriteriaDelMapper_yRetornarSuResultado() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarRevisionesAsesorAsesorQuery.crear(asesor, criterio);
        var resultadoEsperado = PaginatedResult.<RevisionAsesorAsesorReadModel>of(List.of(), 0, 10, 0L);
        when(consultarRevisionesAsesorAsesorUseCase.ejecutar(any(RevisionAsesorAsesorCriteria.class)))
                .thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(consultarRevisionesAsesorAsesorUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().getRaiz()).isEqualTo(
                NodoFiltro.predicado("asesorId", FiltroOperador.ES, asesor.toString()));
    }
}
