package com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilAsesoradasQuery;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.usecase.ConsultarFichasPerfilAsesoradasUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.FiltroOperador;
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
class ConsultarFichasPerfilAsesoradasInteractorImplTest {

    @Mock
    private ConsultarFichasPerfilAsesoradasUseCase consultarFichasPerfilAsesoradasUseCase;

    @Captor
    private ArgumentCaptor<FichaPerfilCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarFichasPerfilAsesoradasInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCaseConElCriteriaDelMapper_yRetornarSuResultado() {
        // Arrange
        var asesorFicha = UUID.randomUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarFichasPerfilAsesoradasQuery.crear(asesorFicha, criterio);

        PaginatedResult<FichaPerfilReadModel> resultadoEsperado = PaginatedResult.of(List.of(), 0, 10, 0L);
        when(consultarFichasPerfilAsesoradasUseCase.ejecutar(any(FichaPerfilCriteria.class)))
                .thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(consultarFichasPerfilAsesoradasUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().getRaiz()).isEqualTo(
                NodoFiltro.predicado("asesorId", FiltroOperador.ES, asesorFicha.toString()));
    }
}
