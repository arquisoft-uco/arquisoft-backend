package com.arquisoft.solicitudes.application.usuario.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.usuario.command.primaryport.model.RegistrarUsuarioCommand;
import com.arquisoft.solicitudes.application.usuario.command.result.AgregacionUsuarioResult;
import com.arquisoft.solicitudes.application.usuario.command.usecase.RegistrarUsuarioUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarUsuarioInteractorImplTest {

    @Mock
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @InjectMocks
    private RegistrarUsuarioInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_cuandoEjecutarEsInvocado() {
        // Arrange
        var id = UUID.randomUUID();
        var command = RegistrarUsuarioCommand.crear(
                id.toString(), "EST-9", "Nombre Completo", "n@uco.edu.co", Instant.now());
        var esperado = new AgregacionUsuarioResult.Agregada(id);
        when(registrarUsuarioUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(registrarUsuarioUseCase).ejecutar(any());
    }
}
