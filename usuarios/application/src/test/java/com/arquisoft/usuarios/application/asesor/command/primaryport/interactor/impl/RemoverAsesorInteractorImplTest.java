package com.arquisoft.usuarios.application.asesor.command.primaryport.interactor.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.asesor.command.primaryport.model.RemoverAsesorCommand;
import com.arquisoft.usuarios.application.asesor.command.usecase.RemoverAsesorUseCase;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RemoverAsesorInteractorImplTest {

    @Mock
    private RemoverAsesorUseCase removerAsesorUseCase;

    @InjectMocks
    private RemoverAsesorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDomain_enRemoverAsesorInteractor() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var command = RemoverAsesorCommand.crear(usuario);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(AsesorDomain.class);
        verify(removerAsesorUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getUsuario()).isEqualTo(usuario);
        assertThat(captor.getValue().estaEliminado()).isFalse();
    }
}
