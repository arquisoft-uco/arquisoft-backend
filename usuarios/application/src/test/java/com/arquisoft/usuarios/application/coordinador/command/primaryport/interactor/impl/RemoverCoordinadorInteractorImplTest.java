package com.arquisoft.usuarios.application.coordinador.command.primaryport.interactor.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.coordinador.command.primaryport.model.RemoverCoordinadorCommand;
import com.arquisoft.usuarios.application.coordinador.command.usecase.RemoverCoordinadorUseCase;
import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RemoverCoordinadorInteractorImplTest {

    @Mock
    private RemoverCoordinadorUseCase removerCoordinadorUseCase;

    @InjectMocks
    private RemoverCoordinadorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDomain_enRemoverCoordinadorInteractor() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var command = RemoverCoordinadorCommand.crear(usuario);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(CoordinadorDomain.class);
        verify(removerCoordinadorUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getUsuario()).isEqualTo(usuario);
        assertThat(captor.getValue().estaEliminado()).isFalse();
    }
}
