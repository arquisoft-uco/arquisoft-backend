package com.arquisoft.fichas.application.estadorevision.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadorevision.query.readmodel.EstadoRevisionReadModel;
import com.arquisoft.fichas.application.estadorevision.query.usecase.ConsultarEstadosRevisionUseCase;
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
class ConsultarEstadosRevisionInteractorImplTest {

    @Mock
    private ConsultarEstadosRevisionUseCase consultarEstadosRevisionUseCase;

    @InjectMocks
    private ConsultarEstadosRevisionInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_cuandoSeEjecuta() {
        // Arrange
        var esperados = List.of(
                new EstadoRevisionReadModel("NUEVA", "Nueva", "Creada recientemente, aun no revisada."));
        when(consultarEstadosRevisionUseCase.ejecutar()).thenReturn(esperados);

        // Act
        var resultado = interactor.ejecutar();

        // Assert
        assertThat(resultado).isSameAs(esperados);
        verify(consultarEstadosRevisionUseCase).ejecutar();
    }
}
