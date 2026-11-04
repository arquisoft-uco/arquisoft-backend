package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.interactor.impl;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.model.GenerarRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.usecase.GenerarRepositorioArtefactoUseCase;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerarRepositorioArtefactoInteractorImplTest {

    @Mock
    private GenerarRepositorioArtefactoUseCase useCase;

    @InjectMocks
    private GenerarRepositorioArtefactoInteractorImpl interactor;

    @Test
    void debeDelegarAlUseCaseConElDomainDelMapper() {
        // Arrange
        var administrador = UtilUUID.generarNuevoUUID();
        var command = GenerarRepositorioArtefactoCommand.crear("Repo", "Descripcion", administrador);
        var esperado = UtilUUID.generarNuevoUUID();
        when(useCase.ejecutar(any(RepositorioArtefactoDomain.class))).thenReturn(esperado);

        // Act
        var id = interactor.ejecutar(command);

        // Assert
        assertThat(id).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(RepositorioArtefactoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getNombreRepositorio()).isEqualTo("Repo");
        assertThat(captor.getValue().getAdministrador()).isEqualTo(administrador);
    }
}
