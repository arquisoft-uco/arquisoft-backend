package com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.EstudianteArtefactoOutputPort;
import com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.DepurarEstudiantesArtefactoUseCase;
import com.arquisoft.artefactos.domain.estudianteartefacto.DepuracionEstudiantesArtefactoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EstudianteArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DepurarEstudiantesArtefactoUseCaseImpl implements DepurarEstudiantesArtefactoUseCase {

    private final EstudianteArtefactoOutputPort estudianteArtefactoOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(DepuracionEstudiantesArtefactoDomain depuracion) {
        logger.debug(EstudianteArtefactoKey.LOG_DEPURANDO, depuracion.getArtefacto());

        estudianteArtefactoOutputPort.eliminarNoVigentesDelArtefacto(depuracion.getArtefacto());
    }
}
