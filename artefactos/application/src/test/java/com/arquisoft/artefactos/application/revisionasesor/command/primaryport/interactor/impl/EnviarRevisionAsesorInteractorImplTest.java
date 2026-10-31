package com.arquisoft.artefactos.application.revisionasesor.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.EnviarRevisionAsesorCommand;
import com.arquisoft.artefactos.application.revisionasesor.command.usecase.EnviarRevisionAsesorUseCase;
import com.arquisoft.artefactos.domain.revisionasesor.EnvioRevisionAsesorDomain;
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
class EnviarRevisionAsesorInteractorImplTest {

    @Mock
    private EnviarRevisionAsesorUseCase useCase;

    @InjectMocks
    private EnviarRevisionAsesorInteractorImpl interactor;

    @Test
    void debeMapearYDelegarEnElUseCase_cuandoSeEjecuta() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var command = EnviarRevisionAsesorCommand.crear(revision, asesor);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(EnvioRevisionAsesorDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getRevisionAsesor()).isEqualTo(revision);
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
    }
}
