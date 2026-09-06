package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.itemfichaperfil.query.criteria.ItemFichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilRepresentanteQuery;
import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.application.itemfichaperfil.query.usecase.ConsultarItemsFichaPerfilRepresentanteUseCase;
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
class ConsultarItemsFichaPerfilRepresentanteInteractorImplTest {

    @Mock
    private ConsultarItemsFichaPerfilRepresentanteUseCase consultarItemsFichaPerfilRepresentanteUseCase;

    @Captor
    private ArgumentCaptor<ItemFichaPerfilRepresentanteCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarItemsFichaPerfilRepresentanteInteractorImpl interactor;

    @Test
    void debeConvertirQueryACriteriaYDelegarEnUseCase() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var representanteComite = UUID.randomUUID();
        var query = ConsultarItemsFichaPerfilRepresentanteQuery.crear(fichaPerfil, representanteComite);
        List<ItemFichaPerfilReadModel> esperado = List.of();
        when(consultarItemsFichaPerfilRepresentanteUseCase.ejecutar(any(ItemFichaPerfilRepresentanteCriteria.class)))
                .thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarItemsFichaPerfilRepresentanteUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(criteriaCaptor.getValue().representanteComite()).isEqualTo(representanteComite);
    }
}
