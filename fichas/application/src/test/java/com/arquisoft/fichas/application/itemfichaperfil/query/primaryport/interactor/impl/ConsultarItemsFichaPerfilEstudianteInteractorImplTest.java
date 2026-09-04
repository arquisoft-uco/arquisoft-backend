package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.itemfichaperfil.query.criteria.ItemFichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilEstudianteQuery;
import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.application.itemfichaperfil.query.usecase.ConsultarItemsFichaPerfilEstudianteUseCase;
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
class ConsultarItemsFichaPerfilEstudianteInteractorImplTest {

    @Mock
    private ConsultarItemsFichaPerfilEstudianteUseCase consultarItemsFichaPerfilEstudianteUseCase;

    @Captor
    private ArgumentCaptor<ItemFichaPerfilEstudianteCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarItemsFichaPerfilEstudianteInteractorImpl interactor;

    @Test
    void debeConvertirQueryACriteriaYDelegarEnUseCase() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var estudiante = UUID.randomUUID();
        var query = ConsultarItemsFichaPerfilEstudianteQuery.crear(fichaPerfil, estudiante);
        List<ItemFichaPerfilReadModel> esperado = List.of();
        when(consultarItemsFichaPerfilEstudianteUseCase.ejecutar(any(ItemFichaPerfilEstudianteCriteria.class)))
                .thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarItemsFichaPerfilEstudianteUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(criteriaCaptor.getValue().estudiante()).isEqualTo(estudiante);
    }
}
