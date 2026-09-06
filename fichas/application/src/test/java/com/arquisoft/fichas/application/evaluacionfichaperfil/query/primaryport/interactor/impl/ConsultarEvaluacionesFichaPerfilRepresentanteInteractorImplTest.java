package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilRepresentanteQuery;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase.ConsultarEvaluacionesFichaPerfilRepresentanteUseCase;
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
class ConsultarEvaluacionesFichaPerfilRepresentanteInteractorImplTest {

    @Mock
    private ConsultarEvaluacionesFichaPerfilRepresentanteUseCase consultarEvaluacionesFichaPerfilRepresentanteUseCase;

    @Captor
    private ArgumentCaptor<EvaluacionFichaPerfilRepresentanteCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarEvaluacionesFichaPerfilRepresentanteInteractorImpl interactor;

    @Test
    void debeConvertirQueryACriteriaYDelegarEnUseCase() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var representanteComite = UUID.randomUUID();
        var query = ConsultarEvaluacionesFichaPerfilRepresentanteQuery.crear(fichaPerfil, representanteComite);
        List<EvaluacionFichaPerfilReadModel> esperado = List.of();
        when(consultarEvaluacionesFichaPerfilRepresentanteUseCase.ejecutar(
                any(EvaluacionFichaPerfilRepresentanteCriteria.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarEvaluacionesFichaPerfilRepresentanteUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(criteriaCaptor.getValue().representanteComite()).isEqualTo(representanteComite);
    }
}
