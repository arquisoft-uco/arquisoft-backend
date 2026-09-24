package com.arquisoft.usuarios.application.usuario.command.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.usuario.command.primaryport.model.ModificarUsuarioCommand;
import com.arquisoft.usuarios.application.usuario.command.usecase.ModificarUsuarioUseCase;
import com.arquisoft.usuarios.domain.usuario.ModificacionUsuarioDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ModificarUsuarioInteractorImplTest {

    @Mock
    private ModificarUsuarioUseCase modificarUsuarioUseCase;

    private ModificarUsuarioInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conLaModificacionMapeadaDelCommand() {
        // Arrange
        interactor = new ModificarUsuarioInteractorImpl(modificarUsuarioUseCase);
        var usuarioId = UUID.randomUUID();
        var datos = new ModificarUsuarioCommand.DatosModificarUsuario(
                null, "Nombre Nuevo", null, null, null, null);
        var command = ModificarUsuarioCommand.crear(usuarioId.toString(), datos, List.of());

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(ModificacionUsuarioDomain.class);
        verify(modificarUsuarioUseCase, times(1)).ejecutar(captor.capture());
        assertThat(captor.getValue().getUsuario()).isEqualTo(usuarioId);
        assertThat(captor.getValue().getNombre()).isEqualTo("Nombre Nuevo");
    }
}
