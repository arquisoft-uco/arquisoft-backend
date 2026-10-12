package com.arquisoft.proyectos.application.asesor.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.asesor.command.primaryport.model.ActualizarAsesorCommand;
import com.arquisoft.proyectos.application.asesor.command.result.ActualizacionAsesorResult;
import com.arquisoft.proyectos.application.asesor.command.usecase.ActualizarAsesorUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarAsesorInteractorImplTest {

    @Mock
    private ActualizarAsesorUseCase actualizarAsesorUseCase;

    private ActualizarAsesorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDominioMapeadoDelCommand() {
        // Arrange
        interactor = new ActualizarAsesorInteractorImpl(actualizarAsesorUseCase);
        var id = UUID.randomUUID();
        var command = ActualizarAsesorCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());
        var resultadoEsperado = new ActualizacionAsesorResult.Actualizada(id);
        when(actualizarAsesorUseCase.ejecutar(ArgumentMatchers.any())).thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(actualizarAsesorUseCase).ejecutar(ArgumentMatchers.argThat(
                d -> d.getId().equals(id) && d.getIdentificador().equals("20161020123")));
    }
}
