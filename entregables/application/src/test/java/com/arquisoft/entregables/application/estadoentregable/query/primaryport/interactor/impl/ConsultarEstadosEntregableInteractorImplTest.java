package com.arquisoft.entregables.application.estadoentregable.query.primaryport.interactor.impl;

import com.arquisoft.entregables.application.estadoentregable.query.readmodel.EstadoEntregableReadModel;
import com.arquisoft.entregables.application.estadoentregable.query.usecase.ConsultarEstadosEntregableUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosEntregableInteractorImplTest {

    @Mock
    private ConsultarEstadosEntregableUseCase useCase;

    @InjectMocks
    private ConsultarEstadosEntregableInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_yRetornarSuResultado() {
        // Arrange
        var esperado = List.of(new EstadoEntregableReadModel("APROBADO", "Aprobado", "d"));
        when(useCase.ejecutar()).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar();

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar();
    }
}
