package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.model.CambiarNombreRepositorioVersionRepositorioArtefactoCommand;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.usecase.CambiarNombreRepositorioVersionRepositorioArtefactoUseCase;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.CambioNombreRepositorioDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CambiarNombreRepositorioVersionRepositorioArtefactoInteractorImplTest {

    @Mock
    private CambiarNombreRepositorioVersionRepositorioArtefactoUseCase useCase;

    @InjectMocks
    private CambiarNombreRepositorioVersionRepositorioArtefactoInteractorImpl interactor;

    @Test
    void debeDelegarYMapear_cuandoSeEjecuta() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var command = CambiarNombreRepositorioVersionRepositorioArtefactoCommand.crear(
                repositorio.toString(), "Repositorio nuevo");

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(CambioNombreRepositorioDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getRepositorio()).isEqualTo(repositorio);
        assertThat(captor.getValue().getNombreRepositorio()).isEqualTo("Repositorio nuevo");
    }
}
