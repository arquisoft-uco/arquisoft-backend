package com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.EstudianteArtefactoOutputPort;
import com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.DesvincularEstudianteArtefactosUseCase;
import com.arquisoft.artefactos.domain.estudianteartefacto.DesvinculacionEstudianteArtefactosDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EstudianteArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DesvincularEstudianteArtefactosUseCaseImpl implements DesvincularEstudianteArtefactosUseCase {

    private final EstudianteArtefactoOutputPort estudianteArtefactoOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(DesvinculacionEstudianteArtefactosDomain desvinculacion) {
        logger.debug(EstudianteArtefactoKey.LOG_DESVINCULANDO,
                desvinculacion.getProyectoGrado(), desvinculacion.getEstudiante());
        estudianteArtefactoOutputPort.eliminarPorProyectoGradoYEstudiante(
                desvinculacion.getProyectoGrado(), desvinculacion.getEstudiante());
    }
}
