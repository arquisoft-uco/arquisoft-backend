package com.arquisoft.biblioteca.application.entregableproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.entregableproyectogrado.query.criteria.EntregableDisponibleCriteria;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.primaryport.model.ConsultarEntregablesDisponiblesQuery;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.readmodel.EntregableDisponibleReadModel;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.usecase.ConsultarEntregablesDisponiblesUseCase;
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
class ConsultarEntregablesDisponiblesInteractorImplTest {

    @Mock
    private ConsultarEntregablesDisponiblesUseCase consultarEntregablesDisponiblesUseCase;

    @InjectMocks
    private ConsultarEntregablesDisponiblesInteractorImpl interactor;

    @Test
    void debeConvertirAlCriteriaYDelegarEnElUseCase_cuandoSeEjecuta() {
        // Arrange
        var query = ConsultarEntregablesDisponiblesQuery.crear(1, 5, "gestión");
        var esperado = PaginatedResult.<EntregableDisponibleReadModel>of(List.of(), 1, 5, 0L);
        when(consultarEntregablesDisponiblesUseCase.ejecutar(any(EntregableDisponibleCriteria.class)))
                .thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(EntregableDisponibleCriteria.class);
        verify(consultarEntregablesDisponiblesUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getPagina()).isEqualTo(1);
        assertThat(captor.getValue().getTamanio()).isEqualTo(5);
        assertThat(captor.getValue().tieneFiltros()).isTrue();
    }
}
