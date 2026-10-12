package com.arquisoft.usuarios.application.coordinador.query.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.coordinador.query.criteria.CoordinadorVigenteCriteria;
import com.arquisoft.usuarios.application.coordinador.query.readmodel.CoordinadorVigenteReadModel;
import com.arquisoft.usuarios.application.coordinador.query.usecase.ConsultarCoordinadoresVigentesUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarCoordinadoresVigentesInteractorImplTest {

    @Mock
    private ConsultarCoordinadoresVigentesUseCase useCase;

    @InjectMocks
    private ConsultarCoordinadoresVigentesInteractorImpl interactor;

    @Test
    void debeMapearLaEntradaYDelegarEnElUseCase_yRetornarSuResultado() {
        // Arrange
        var entrada = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var esperado = PaginatedResult.<CoordinadorVigenteReadModel>of(List.of(), 0, 10, 0L);
        when(useCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(entrada);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(CoordinadorVigenteCriteria.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getPagina()).isEqualTo(0);
        assertThat(captor.getValue().getTamanio()).isEqualTo(10);
    }
}
