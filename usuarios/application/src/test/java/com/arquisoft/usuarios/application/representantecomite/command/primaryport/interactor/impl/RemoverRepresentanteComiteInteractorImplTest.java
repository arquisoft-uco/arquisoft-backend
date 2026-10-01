package com.arquisoft.usuarios.application.representantecomite.command.primaryport.interactor.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.representantecomite.command.primaryport.model.RemoverRepresentanteComiteCommand;
import com.arquisoft.usuarios.application.representantecomite.command.usecase.RemoverRepresentanteComiteUseCase;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RemoverRepresentanteComiteInteractorImplTest {

    @Mock
    private RemoverRepresentanteComiteUseCase removerRepresentanteComiteUseCase;

    @InjectMocks
    private RemoverRepresentanteComiteInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDomain_enRemoverRepresentanteComiteInteractor() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var command = RemoverRepresentanteComiteCommand.crear(usuario);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(RepresentanteComiteDomain.class);
        verify(removerRepresentanteComiteUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getUsuario()).isEqualTo(usuario);
        assertThat(captor.getValue().estaEliminado()).isFalse();
    }
}
