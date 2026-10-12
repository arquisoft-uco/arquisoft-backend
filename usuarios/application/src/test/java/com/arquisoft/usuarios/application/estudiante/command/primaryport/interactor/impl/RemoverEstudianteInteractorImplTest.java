package com.arquisoft.usuarios.application.estudiante.command.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.estudiante.command.primaryport.model.RemoverEstudianteCommand;
import com.arquisoft.usuarios.application.estudiante.command.usecase.RemoverEstudianteUseCase;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RemoverEstudianteInteractorImplTest {

    @Mock
    private RemoverEstudianteUseCase removerEstudianteUseCase;

    @InjectMocks
    private RemoverEstudianteInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDomain_enRemoverEstudianteInteractor() {
        // Arrange
        var usuario = UUID.randomUUID();
        var command = RemoverEstudianteCommand.crear(usuario);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(EstudianteDomain.class);
        verify(removerEstudianteUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getUsuario()).isEqualTo(usuario);
        assertThat(captor.getValue().estaEliminado()).isFalse();
    }
}
