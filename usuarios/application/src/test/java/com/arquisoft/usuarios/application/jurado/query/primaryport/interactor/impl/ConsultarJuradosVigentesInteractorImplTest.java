package com.arquisoft.usuarios.application.jurado.query.primaryport.interactor.impl;

import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.usuarios.application.jurado.query.criteria.JuradoVigenteCriteria;
import com.arquisoft.usuarios.application.jurado.query.readmodel.JuradoVigenteReadModel;
import com.arquisoft.usuarios.application.jurado.query.usecase.ConsultarJuradosVigentesUseCase;
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
class ConsultarJuradosVigentesInteractorImplTest {

    @Mock
    private ConsultarJuradosVigentesUseCase useCase;

    @InjectMocks
    private ConsultarJuradosVigentesInteractorImpl interactor;

    @Test
    void debeConvertirQueryYDelegarEnElUseCase_cuandoEjecuta() {
        // Arrange
        var entrada = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var esperado = PaginatedResult.<JuradoVigenteReadModel>of(List.of(), 0, 10, 0L);
        when(useCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(entrada);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(JuradoVigenteCriteria.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getPagina()).isZero();
        assertThat(captor.getValue().getTamanio()).isEqualTo(10);
    }
}
