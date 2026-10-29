package com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoCriteria;
import com.arquisoft.entregables.application.entregableproyectogrado.query.readmodel.EntregableProyectoGradoReadModel;
import com.arquisoft.entregables.application.entregableproyectogrado.query.usecase.ConsultarEntregablesProyectoGradoAsesorUseCase;
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
class ConsultarEntregablesProyectoGradoAsesorInteractorImplTest {

    @Mock
    private ConsultarEntregablesProyectoGradoAsesorUseCase useCase;

    @InjectMocks
    private ConsultarEntregablesProyectoGradoAsesorInteractorImpl interactor;

    @Test
    void debeConvertirLaQueryEnCriteria_yDelegarEnElUseCase() {
        // Arrange
        var query = ConsultaCriteriaQuery.crear(1, 20, List.of(), null);
        var esperado = PaginatedResult.<EntregableProyectoGradoReadModel>of(List.of(), 1, 20, 0L);
        when(useCase.ejecutar(any(EntregableProyectoGradoCriteria.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        var captor = ArgumentCaptor.forClass(EntregableProyectoGradoCriteria.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getPagina()).isEqualTo(1);
        assertThat(captor.getValue().getTamanio()).isEqualTo(20);
        assertThat(resultado).isSameAs(esperado);
    }
}
