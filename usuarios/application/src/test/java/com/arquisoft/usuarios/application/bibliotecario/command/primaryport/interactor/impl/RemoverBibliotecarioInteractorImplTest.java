package com.arquisoft.usuarios.application.bibliotecario.command.primaryport.interactor.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.bibliotecario.command.primaryport.model.RemoverBibliotecarioCommand;
import com.arquisoft.usuarios.application.bibliotecario.command.usecase.RemoverBibliotecarioUseCase;
import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RemoverBibliotecarioInteractorImplTest {

    @Mock
    private RemoverBibliotecarioUseCase removerBibliotecarioUseCase;

    @InjectMocks
    private RemoverBibliotecarioInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDomain_enRemoverBibliotecarioInteractor() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var command = RemoverBibliotecarioCommand.crear(usuario);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(BibliotecarioDomain.class);
        verify(removerBibliotecarioUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getUsuario()).isEqualTo(usuario);
        assertThat(captor.getValue().estaEliminado()).isFalse();
    }
}
