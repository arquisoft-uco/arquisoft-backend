package com.arquisoft.proyectos.application.estadoasesor.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estadoasesor.query.readmodel.EstadoAsesorReadModel;
import com.arquisoft.proyectos.application.estadoasesor.query.usecase.ConsultarEstadosAsesorUseCase;
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
class ConsultarEstadosAsesorInteractorImplTest {

    @Mock
    private ConsultarEstadosAsesorUseCase useCase;

    @InjectMocks
    private ConsultarEstadosAsesorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_cuandoSeEjecuta() {
        // Arrange
        var esperado = List.of(new EstadoAsesorReadModel("ASIGNADO", "Asignado", "Asesor asignado"));
        when(useCase.ejecutar()).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar();

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar();
    }
}
