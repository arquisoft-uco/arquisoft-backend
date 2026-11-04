package com.arquisoft.mapas_ruta.application.estadoitem.query.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.estadoitem.query.readmodel.EstadoItemReadModel;
import com.arquisoft.mapas_ruta.application.estadoitem.query.usecase.ConsultarEstadosItemUseCase;
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
class ConsultarEstadosItemInteractorImplTest {

    @Mock
    private ConsultarEstadosItemUseCase useCase;

    @InjectMocks
    private ConsultarEstadosItemInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_cuandoSeEjecuta() {
        // Arrange
        var esperado = List.of(new EstadoItemReadModel("NUEVA", "Nueva", "Sin iniciar"));
        when(useCase.ejecutar()).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar();

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar();
    }
}
