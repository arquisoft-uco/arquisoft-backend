package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.interactor.impl;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.model.ModificarRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.usecase.ModificarRepositorioArtefactoUseCase;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.ModificacionRepositorioArtefactoDomain;
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
class ModificarRepositorioArtefactoInteractorImplTest {

    @Mock
    private ModificarRepositorioArtefactoUseCase useCase;

    @InjectMocks
    private ModificarRepositorioArtefactoInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCase_cuandoSeEjecuta() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();
        var command = ModificarRepositorioArtefactoCommand.crear(
                repositorio, "Repo", "Descripcion", administrador);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(ModificacionRepositorioArtefactoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getRepositorio()).isEqualTo(repositorio);
        assertThat(captor.getValue().getAdministrador()).isEqualTo(administrador);
    }
}
