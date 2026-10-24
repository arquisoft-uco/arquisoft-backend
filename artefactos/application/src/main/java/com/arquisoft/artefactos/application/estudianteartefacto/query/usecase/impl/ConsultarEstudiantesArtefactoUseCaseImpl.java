package com.arquisoft.artefactos.application.estudianteartefacto.query.usecase.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.query.criteria.EstudianteArtefactoCriteria;
import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.EstudianteArtefactoReadModel;
import com.arquisoft.artefactos.application.estudianteartefacto.query.secondaryport.EstudianteArtefactoQueryOutputPort;
import com.arquisoft.artefactos.application.estudianteartefacto.query.usecase.ConsultarEstudiantesArtefactoUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EstudianteArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstudiantesArtefactoUseCaseImpl implements ConsultarEstudiantesArtefactoUseCase {

    private final EstudianteArtefactoQueryOutputPort estudianteArtefactoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstudianteArtefactoReadModel> ejecutar(EstudianteArtefactoCriteria entrada) {
        logger.debug(EstudianteArtefactoKey.LOG_CONSULTANDO, entrada.artefacto());

        var estudiantes = estudianteArtefactoQueryOutputPort.consultarPorArtefacto(entrada.artefacto());

        logger.debug(EstudianteArtefactoKey.LOG_CONSULTA_COMPLETADA, estudiantes.size());
        return estudiantes;
    }
}
