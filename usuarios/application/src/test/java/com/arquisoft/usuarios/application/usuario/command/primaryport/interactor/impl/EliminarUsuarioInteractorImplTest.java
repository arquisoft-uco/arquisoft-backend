package com.arquisoft.usuarios.application.usuario.command.primaryport.interactor.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.usuario.command.primaryport.model.EliminarUsuarioCommand;
import com.arquisoft.usuarios.application.usuario.command.usecase.EliminarUsuarioUseCase;
import com.arquisoft.usuarios.domain.usuario.EliminacionUsuarioDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EliminarUsuarioInteractorImplTest {

    @Mock
    private EliminarUsuarioUseCase eliminarUsuarioUseCase;

    @InjectMocks
    private EliminarUsuarioInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDomainMapeado_cuandoSeEjecuta() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        interactor.ejecutar(EliminarUsuarioCommand.crear(usuario));

        // Assert
        var captor = ArgumentCaptor.forClass(EliminacionUsuarioDomain.class);
        verify(eliminarUsuarioUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getUsuario()).isEqualTo(usuario);
    }
}
