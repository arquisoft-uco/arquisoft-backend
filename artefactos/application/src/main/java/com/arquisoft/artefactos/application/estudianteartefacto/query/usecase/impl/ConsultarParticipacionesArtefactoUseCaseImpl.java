package com.arquisoft.artefactos.application.estudianteartefacto.query.usecase.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.query.criteria.ParticipacionArtefactoCriteria;
import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.ParticipacionArtefactoReadModel;
import com.arquisoft.artefactos.application.estudianteartefacto.query.secondaryport.EstudianteArtefactoQueryOutputPort;
import com.arquisoft.artefactos.application.estudianteartefacto.query.usecase.ConsultarParticipacionesArtefactoUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EstudianteArtefactoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ConsultarParticipacionesArtefactoUseCaseImpl implements ConsultarParticipacionesArtefactoUseCase {

    private final EstudianteArtefactoQueryOutputPort estudianteArtefactoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<ParticipacionArtefactoReadModel> ejecutar(ParticipacionArtefactoCriteria entrada) {
        logger.debug(EstudianteArtefactoKey.LOG_CONSULTANDO_PARTICIPACION,
                entrada.getPagina(), entrada.getTamanio(), entrada.tieneFiltros(), entrada.tieneOrden());

        var participaciones = estudianteArtefactoQueryOutputPort.consultarParticipaciones(entrada);

        logger.debug(EstudianteArtefactoKey.LOG_CONSULTA_PARTICIPACION_COMPLETADA, participaciones.getTotalElements());
        return participaciones;
    }
}
