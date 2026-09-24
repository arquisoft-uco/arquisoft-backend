package com.arquisoft.solicitudes.application.usuario.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.usuario.command.primaryport.model.ActualizarUsuarioCommand;
import com.arquisoft.solicitudes.application.usuario.command.result.ActualizacionUsuarioResult;
import com.arquisoft.solicitudes.application.usuario.command.usecase.ActualizarUsuarioUseCase;
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
class ActualizarUsuarioInteractorImplTest {

    @Mock
    private ActualizarUsuarioUseCase actualizarUsuarioUseCase;

    private ActualizarUsuarioInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDominioMapeadoDelCommand() {
        // Arrange
        interactor = new ActualizarUsuarioInteractorImpl(actualizarUsuarioUseCase);
        var id = UUID.randomUUID();
        var command = ActualizarUsuarioCommand.crear(
                id.toString(), "EST-001", "Ana Estudiante", "ana@uco.edu.co", Instant.now());
        var resultadoEsperado = new ActualizacionUsuarioResult.Actualizada(id);
        when(actualizarUsuarioUseCase.ejecutar(ArgumentMatchers.any())).thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(actualizarUsuarioUseCase).ejecutar(ArgumentMatchers.argThat(
                d -> d.getId().equals(id) && d.getIdentificador().equals("EST-001")));
    }
}
