package com.arquisoft.artefactos.application.versionartefacto.query.usecase.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoEstudianteCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.VersionArtefactoQueryOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.query.usecase.ConsultarVersionesArtefactoEstudianteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarVersionesArtefactoEstudianteUseCaseImpl implements ConsultarVersionesArtefactoEstudianteUseCase {

    private final VersionArtefactoQueryOutputPort versionArtefactoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<VersionArtefactoReadModel> ejecutar(VersionArtefactoEstudianteCriteria entrada) {
        logger.debug(VersionArtefactoKey.LOG_CONSULTANDO_ESTUDIANTE,
                entrada.getPagina(), entrada.getTamanio(), entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = versionArtefactoQueryOutputPort.consultarTodasEstudiante(entrada);

        logger.debug(VersionArtefactoKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA,
                resultado.getTotalElements());
        return resultado;
    }
}
