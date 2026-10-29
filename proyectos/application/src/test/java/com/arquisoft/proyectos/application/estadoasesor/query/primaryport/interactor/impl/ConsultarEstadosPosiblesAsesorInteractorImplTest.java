package com.arquisoft.proyectos.application.estadoasesor.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estadoasesor.query.criteria.EstadoAsesorCriteria;
import com.arquisoft.proyectos.application.estadoasesor.query.readmodel.EstadoAsesorReadModel;
import com.arquisoft.proyectos.application.estadoasesor.query.usecase.ConsultarEstadosPosiblesAsesorUseCase;
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
class ConsultarEstadosPosiblesAsesorInteractorImplTest {

    @Mock
    private ConsultarEstadosPosiblesAsesorUseCase consultarEstadosPosiblesAsesorUseCase;

    @InjectMocks
    private ConsultarEstadosPosiblesAsesorInteractorImpl interactor;

    @Test
    void debeConvertirQueryACriteriaYDelegar_cuandoSeEjecuta() {
        // Arrange
        var query = ConsultaCriteriaQuery.crear(3, 20, List.of(), null);
        var esperado = PaginatedResult.of(List.<EstadoAsesorReadModel>of(), 3, 20, 0L);
        when(consultarEstadosPosiblesAsesorUseCase.ejecutar(any(EstadoAsesorCriteria.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(EstadoAsesorCriteria.class);
        verify(consultarEstadosPosiblesAsesorUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getPagina()).isEqualTo(3);
        assertThat(captor.getValue().getTamanio()).isEqualTo(20);
    }
}
