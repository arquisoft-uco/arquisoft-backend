package com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilRepresentanteQuery;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilRepresentanteReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.usecase.ConsultarFichasPerfilRepresentanteUseCase;
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
class ConsultarFichasPerfilRepresentanteInteractorImplTest {

    @Mock
    private ConsultarFichasPerfilRepresentanteUseCase consultarFichasPerfilRepresentanteUseCase;

    @Captor
    private ArgumentCaptor<FichaPerfilRepresentanteCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarFichasPerfilRepresentanteInteractorImpl interactor;

    @Test
    void debeConvertirElQueryYDelegarEnElUseCase() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarFichasPerfilRepresentanteQuery.crear(representante, criterio);
        var esperado = PaginatedResult.<FichaPerfilRepresentanteReadModel>of(List.of(), 0, 10, 0L);
        when(consultarFichasPerfilRepresentanteUseCase.ejecutar(any(FichaPerfilRepresentanteCriteria.class)))
                .thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarFichasPerfilRepresentanteUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().getRepresentanteComite()).isEqualTo(representante);
    }
}
