package com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.EstudianteArtefactoOutputPort;
import com.arquisoft.artefactos.domain.estudianteartefacto.DesvinculacionEstudianteArtefactosDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DesvincularEstudianteArtefactosUseCaseImplTest {

    @Mock
    private EstudianteArtefactoOutputPort estudianteArtefactoOutputPort;
    @Mock
    private AppLogger logger;

    @Test
    void debeEliminarPorProyectoYEstudiante_cuandoSeDesvincula() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var useCase = new DesvincularEstudianteArtefactosUseCaseImpl(estudianteArtefactoOutputPort, logger);

        // Act
        useCase.ejecutar(DesvinculacionEstudianteArtefactosDomain.crear(proyecto, estudiante));

        // Assert
        verify(estudianteArtefactoOutputPort, times(1)).eliminarPorProyectoGradoYEstudiante(proyecto, estudiante);
    }
}
