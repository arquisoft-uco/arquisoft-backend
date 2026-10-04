package com.arquisoft.proyectos.application.estudiante.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estudiante.command.primaryport.model.ActualizarEstudianteCommand;
import com.arquisoft.proyectos.application.estudiante.command.result.ActualizacionEstudianteResult;
import com.arquisoft.proyectos.application.estudiante.command.usecase.ActualizarEstudianteUseCase;
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
class ActualizarEstudianteInteractorImplTest {

    @Mock
    private ActualizarEstudianteUseCase actualizarEstudianteUseCase;

    private ActualizarEstudianteInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDominioMapeadoDelCommand() {
        // Arrange
        interactor = new ActualizarEstudianteInteractorImpl(actualizarEstudianteUseCase);
        var id = UUID.randomUUID();
        var command = ActualizarEstudianteCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());
        var resultadoEsperado = new ActualizacionEstudianteResult.Actualizada(id);
        when(actualizarEstudianteUseCase.ejecutar(ArgumentMatchers.any())).thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(actualizarEstudianteUseCase).ejecutar(ArgumentMatchers.argThat(
                d -> d.getId().equals(id) && d.getIdentificador().equals("20161020123")));
    }
}
