package com.arquisoft.artefactos.application.estadorevisionasesor.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.estadorevisionasesor.query.readmodel.EstadoRevisionAsesorReadModel;
import com.arquisoft.artefactos.application.estadorevisionasesor.query.usecase.ConsultarEstadosRevisionAsesorUseCase;
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
class ConsultarEstadosRevisionAsesorInteractorImplTest {

    @Mock
    private ConsultarEstadosRevisionAsesorUseCase useCase;

    @InjectMocks
    private ConsultarEstadosRevisionAsesorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_cuandoEjecuta() {
        // Arrange
        var esperado = List.of(new EstadoRevisionAsesorReadModel("PENDIENTE", "Pendiente", "Nueva"));
        when(useCase.ejecutar()).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar();

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar();
    }
}
