package com.arquisoft.usuarios.application.administrador.command.primaryport.interactor.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.administrador.command.primaryport.model.RemoverAdministradorCommand;
import com.arquisoft.usuarios.application.administrador.command.usecase.RemoverAdministradorUseCase;
import com.arquisoft.usuarios.domain.administrador.RemocionAdministradorDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RemoverAdministradorInteractorImplTest {

    @Mock
    private RemoverAdministradorUseCase removerAdministradorUseCase;

    @Test
    void debeDelegarEnElUseCase_conElDominioMapeado() {
        // Arrange
        var interactor = new RemoverAdministradorInteractorImpl(removerAdministradorUseCase);
        var usuario = UtilUUID.generarNuevoUUID();
        var actor = UtilUUID.generarNuevoUUID();
        var comando = RemoverAdministradorCommand.crear(usuario, actor);

        // Act
        interactor.ejecutar(comando);

        // Assert
        var captor = ArgumentCaptor.forClass(RemocionAdministradorDomain.class);
        verify(removerAdministradorUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getUsuario()).isEqualTo(usuario);
        assertThat(captor.getValue().getActor()).isEqualTo(actor);
    }
}
