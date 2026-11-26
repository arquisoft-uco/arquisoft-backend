package com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilDisponibleEvaluacionCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilDisponiblesEvaluacionQuery;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.usecase.ConsultarFichasPerfilDisponiblesEvaluacionUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
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
class ConsultarFichasPerfilDisponiblesEvaluacionInteractorImplTest {

    @Mock
    private ConsultarFichasPerfilDisponiblesEvaluacionUseCase consultarFichasPerfilDisponiblesEvaluacionUseCase;

    @Captor
    private ArgumentCaptor<FichaPerfilDisponibleEvaluacionCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarFichasPerfilDisponiblesEvaluacionInteractorImpl interactor;

    @Test
    void debeConvertirElQueryEnCriteriaYDelegarEnElUseCase() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarFichasPerfilDisponiblesEvaluacionQuery.crear(representante, criterio);
        var esperado = PaginatedResult.<FichaPerfilReadModel>of(List.of(), 0, 10, 0L);
        when(consultarFichasPerfilDisponiblesEvaluacionUseCase.ejecutar(any(FichaPerfilDisponibleEvaluacionCriteria.class)))
                .thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarFichasPerfilDisponiblesEvaluacionUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().getRepresentanteComite()).isEqualTo(representante);
    }
}
