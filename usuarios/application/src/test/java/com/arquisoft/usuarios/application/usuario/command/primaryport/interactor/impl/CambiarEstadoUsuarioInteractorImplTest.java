package com.arquisoft.usuarios.application.usuario.command.primaryport.interactor.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.usuario.command.primaryport.model.CambiarEstadoUsuarioCommand;
import com.arquisoft.usuarios.application.usuario.command.usecase.CambiarEstadoUsuarioUseCase;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.CambioEstadoUsuarioDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CambiarEstadoUsuarioInteractorImplTest {

    @Mock
    private CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase;

    @InjectMocks
    private CambiarEstadoUsuarioInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDomainMapeado_cuandoSeEjecuta() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        interactor.ejecutar(CambiarEstadoUsuarioCommand.crear(usuario, "INACTIVO"));

        // Assert
        var captor = ArgumentCaptor.forClass(CambioEstadoUsuarioDomain.class);
        verify(cambiarEstadoUsuarioUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getUsuario()).isEqualTo(usuario);
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoUsuario.INACTIVO);
    }
}
