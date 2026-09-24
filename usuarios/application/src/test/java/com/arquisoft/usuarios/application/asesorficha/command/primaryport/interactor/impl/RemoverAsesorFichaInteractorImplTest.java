package com.arquisoft.usuarios.application.asesorficha.command.primaryport.interactor.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.asesorficha.command.primaryport.model.RemoverAsesorFichaCommand;
import com.arquisoft.usuarios.application.asesorficha.command.usecase.RemoverAsesorFichaUseCase;
import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RemoverAsesorFichaInteractorImplTest {

    @Mock
    private RemoverAsesorFichaUseCase removerAsesorFichaUseCase;

    @InjectMocks
    private RemoverAsesorFichaInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDomain_enRemoverAsesorFichaInteractor() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var command = RemoverAsesorFichaCommand.crear(usuario);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(AsesorFichaDomain.class);
        verify(removerAsesorFichaUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getUsuario()).isEqualTo(usuario);
        assertThat(captor.getValue().estaEliminado()).isFalse();
    }
}
