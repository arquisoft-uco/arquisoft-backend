package com.arquisoft.artefactos.application.artefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.domain.artefacto.TraspasoArtefactosDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TraspasarArtefactosUseCaseImplTest {

    @Mock
    private ArtefactoOutputPort artefactoOutputPort;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private TraspasarArtefactosUseCaseImpl useCase;

    @Test
    void debeTraspasarAlPuerto_conProyectoYAsesorDelDomain() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var traspaso = TraspasoArtefactosDomain.crear(proyecto, asesor);

        // Act
        useCase.ejecutar(traspaso);

        // Assert
        verify(artefactoOutputPort, times(1)).traspasarAsesorDeProyecto(proyecto, asesor);
    }
}
