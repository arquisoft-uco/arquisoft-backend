package com.arquisoft.solicitudes.application.usuario.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.solicitudes.application.usuario.command.primaryport.model.RegistrarUsuarioCommand;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.UsuarioOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarUsuarioUseCaseImplTest {

    @Mock
    private UsuarioOutputPort usuarioOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RegistrarUsuarioUseCaseImpl useCase;

    private RegistrarUsuarioCommand comando() {
        return new RegistrarUsuarioCommand(UUID.randomUUID(), "EST-9", "Nombre Completo", "n@uco.edu.co");
    }

    @Test
    void debeRegistrar_cuandoElUsuarioNoExisteEnLaReplica() {
        // Arrange
        RegistrarUsuarioCommand comando = comando();
        when(usuarioOutputPort.existePorId(comando.usuarioId())).thenReturn(false);

        // Act
        useCase.ejecutar(comando);

        // Assert
        verify(usuarioOutputPort).registrar(argThat(e -> e.id().equals(comando.usuarioId())
                && e.identificador().equals("EST-9")
                && e.nombre().equals("Nombre Completo")));
        verify(usuarioOutputPort, never()).actualizar(any());
    }

    @Test
    void debeActualizar_cuandoElUsuarioYaExisteEnLaReplica() {
        // Arrange
        RegistrarUsuarioCommand comando = comando();
        when(usuarioOutputPort.existePorId(comando.usuarioId())).thenReturn(true);

        // Act
        useCase.ejecutar(comando);

        // Assert
        verify(usuarioOutputPort).actualizar(argThat(e -> e.id().equals(comando.usuarioId())));
        verify(usuarioOutputPort, never()).registrar(any());
    }
}
