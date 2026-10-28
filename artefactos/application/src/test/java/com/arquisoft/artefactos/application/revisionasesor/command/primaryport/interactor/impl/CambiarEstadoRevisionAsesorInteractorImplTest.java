package com.arquisoft.artefactos.application.revisionasesor.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.CambiarEstadoRevisionAsesorCommand;
import com.arquisoft.artefactos.application.revisionasesor.command.usecase.CambiarEstadoRevisionAsesorUseCase;
import com.arquisoft.artefactos.domain.revisionasesor.CambioEstadoRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
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
class CambiarEstadoRevisionAsesorInteractorImplTest {

    @Mock
    private CambiarEstadoRevisionAsesorUseCase useCase;

    @InjectMocks
    private CambiarEstadoRevisionAsesorInteractorImpl interactor;

    @Test
    void debeMapearYDelegar_cuandoSeEjecuta() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var command = CambiarEstadoRevisionAsesorCommand.crear(revision, "EN_PROGRESO", estudiante);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(CambioEstadoRevisionAsesorDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getRevisionAsesor()).isEqualTo(revision);
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoRevisionAsesor.EN_PROGRESO);
        assertThat(captor.getValue().getEstudiante()).isEqualTo(estudiante);
    }
}
