package com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.EliminarVersionArtefactoCommand;
import com.arquisoft.artefactos.application.versionartefacto.command.usecase.EliminarVersionArtefactoUseCase;
import com.arquisoft.artefactos.domain.versionartefacto.EliminacionVersionArtefactoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EliminarVersionArtefactoInteractorImplTest {

    @Mock
    private EliminarVersionArtefactoUseCase useCase;

    @Test
    void debeDelegar_conElDomainMapeadoDelCommand() {
        // Arrange
        var interactor = new EliminarVersionArtefactoInteractorImpl(useCase);
        var version = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();
        var command = EliminarVersionArtefactoCommand.crear(version, solicitante);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(EliminacionVersionArtefactoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getVersionArtefacto()).isEqualTo(version);
        assertThat(captor.getValue().getSolicitante()).isEqualTo(solicitante);
    }
}
