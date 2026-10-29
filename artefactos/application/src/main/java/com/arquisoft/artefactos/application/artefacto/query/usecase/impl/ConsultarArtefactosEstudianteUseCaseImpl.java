package com.arquisoft.artefactos.application.artefacto.query.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoEstudianteCriteria;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoEstudianteReadModel;
import com.arquisoft.artefactos.application.artefacto.query.secondaryport.ArtefactoQueryOutputPort;
import com.arquisoft.artefactos.application.artefacto.query.usecase.ConsultarArtefactosEstudianteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarArtefactosEstudianteUseCaseImpl implements ConsultarArtefactosEstudianteUseCase {

    private final ArtefactoQueryOutputPort artefactoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<ArtefactoEstudianteReadModel> ejecutar(ArtefactoEstudianteCriteria entrada) {
        logger.debug(ArtefactoKey.LOG_CONSULTANDO_ESTUDIANTE,
                entrada.getPagina(), entrada.getTamanio(), entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = artefactoQueryOutputPort.consultarTodosEstudiante(entrada);

        logger.debug(ArtefactoKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA, resultado.getTotalElements());
        return resultado;
    }
}
