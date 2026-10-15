package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilAsesorCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilAsesorQuery;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilAsesorReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase.ConsultarEvaluacionesFichaPerfilAsesorUseCase;
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
class ConsultarEvaluacionesFichaPerfilAsesorInteractorImplTest {

    @Mock
    private ConsultarEvaluacionesFichaPerfilAsesorUseCase consultarEvaluacionesFichaPerfilAsesorUseCase;

    @Captor
    private ArgumentCaptor<EvaluacionFichaPerfilAsesorCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarEvaluacionesFichaPerfilAsesorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElCriteriaDelMapper_yRetornarSuResultado() {
        // Arrange
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarEvaluacionesFichaPerfilAsesorQuery.crear(asesorFicha, criterio);
        var resultadoEsperado = PaginatedResult.of(List.<EvaluacionFichaPerfilAsesorReadModel>of(), 0, 10, 0L);
        when(consultarEvaluacionesFichaPerfilAsesorUseCase.ejecutar(any(EvaluacionFichaPerfilAsesorCriteria.class)))
                .thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(consultarEvaluacionesFichaPerfilAsesorUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().getRaiz()).isEqualTo(
                NodoFiltro.predicado("asesorFicha", FiltroOperador.ES, asesorFicha.toString()));
    }
}
