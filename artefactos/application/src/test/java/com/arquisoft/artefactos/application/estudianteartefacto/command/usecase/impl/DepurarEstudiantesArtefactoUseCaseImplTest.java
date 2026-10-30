package com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.EstudianteArtefactoOutputPort;
import com.arquisoft.artefactos.domain.estudianteartefacto.DepuracionEstudiantesArtefactoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EstudianteArtefactoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DepurarEstudiantesArtefactoUseCaseImplTest {

    @Mock
    private EstudianteArtefactoOutputPort estudianteArtefactoOutputPort;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private DepurarEstudiantesArtefactoUseCaseImpl useCase;

    @Test
    void debeEliminarLosVinculosNoVigentes_cuandoSeDepuraElArtefacto() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();

        // Act
        useCase.ejecutar(DepuracionEstudiantesArtefactoDomain.crear(artefacto));

        // Assert
        verify(logger).debug(EstudianteArtefactoKey.LOG_DEPURANDO, artefacto);
        verify(estudianteArtefactoOutputPort).eliminarNoVigentesDelArtefacto(artefacto);
    }
}
