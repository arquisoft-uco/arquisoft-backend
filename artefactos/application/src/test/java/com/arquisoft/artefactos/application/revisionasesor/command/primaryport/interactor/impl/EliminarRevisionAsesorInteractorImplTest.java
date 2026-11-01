package com.arquisoft.artefactos.application.revisionasesor.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.EliminarRevisionAsesorCommand;
import com.arquisoft.artefactos.application.revisionasesor.command.usecase.EliminarRevisionAsesorUseCase;
import com.arquisoft.artefactos.domain.revisionasesor.EliminacionRevisionAsesorDomain;
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
class EliminarRevisionAsesorInteractorImplTest {

    @Mock
    private EliminarRevisionAsesorUseCase useCase;

    @InjectMocks
    private EliminarRevisionAsesorInteractorImpl interactor;

    @Test
    void debeDelegarAlUseCase_conElDominioMapeado() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var command = EliminarRevisionAsesorCommand.crear(revision, asesor);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(EliminacionRevisionAsesorDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getRevisionAsesor()).isEqualTo(revision);
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
    }
}
