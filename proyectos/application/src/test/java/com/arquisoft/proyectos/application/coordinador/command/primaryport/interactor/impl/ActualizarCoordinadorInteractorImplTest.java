package com.arquisoft.proyectos.application.coordinador.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.coordinador.command.primaryport.model.ActualizarCoordinadorCommand;
import com.arquisoft.proyectos.application.coordinador.command.result.ActualizacionCoordinadorResult;
import com.arquisoft.proyectos.application.coordinador.command.usecase.ActualizarCoordinadorUseCase;
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
class ActualizarCoordinadorInteractorImplTest {

    @Mock
    private ActualizarCoordinadorUseCase actualizarCoordinadorUseCase;

    private ActualizarCoordinadorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDominioMapeadoDelCommand() {
        // Arrange
        interactor = new ActualizarCoordinadorInteractorImpl(actualizarCoordinadorUseCase);
        var id = UUID.randomUUID();
        var command = ActualizarCoordinadorCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());
        var resultadoEsperado = new ActualizacionCoordinadorResult.Actualizada(id);
        when(actualizarCoordinadorUseCase.ejecutar(ArgumentMatchers.any())).thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(actualizarCoordinadorUseCase).ejecutar(ArgumentMatchers.argThat(
                d -> d.getId().equals(id) && d.getIdentificador().equals("20161020123")));
    }
}
