package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.itemfichaperfil.query.criteria.ItemFichaPerfilCoordinadorCriteria;
import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilCoordinadorQuery;
import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.application.itemfichaperfil.query.usecase.ConsultarItemsFichaPerfilCoordinadorUseCase;
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
class ConsultarItemsFichaPerfilCoordinadorInteractorImplTest {

    @Mock
    private ConsultarItemsFichaPerfilCoordinadorUseCase consultarItemsFichaPerfilCoordinadorUseCase;

    @Captor
    private ArgumentCaptor<ItemFichaPerfilCoordinadorCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarItemsFichaPerfilCoordinadorInteractorImpl interactor;

    @Test
    void debeConvertirYDelegarAlUseCase_cuandoQueryValida() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var query = ConsultarItemsFichaPerfilCoordinadorQuery.crear(fichaPerfil);
        List<ItemFichaPerfilReadModel> esperado = List.of();
        when(consultarItemsFichaPerfilCoordinadorUseCase.ejecutar(any(ItemFichaPerfilCoordinadorCriteria.class)))
                .thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarItemsFichaPerfilCoordinadorUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().fichaPerfil()).isEqualTo(fichaPerfil);
    }
}
