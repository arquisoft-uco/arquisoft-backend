package com.arquisoft.entregables.application.artefactoentregable.query.usecase.impl;

import com.arquisoft.entregables.application.artefactoentregable.query.criteria.ArtefactoEntregableCriteria;
import com.arquisoft.entregables.application.artefactoentregable.query.readmodel.ArtefactoEntregableReadModel;
import com.arquisoft.entregables.application.artefactoentregable.query.secondaryport.ArtefactoEntregableQueryOutputPort;
import com.arquisoft.entregables.application.artefactoentregable.query.usecase.ConsultarArtefactosEntregableUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoEntregableKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarArtefactosEntregableUseCaseImpl implements ConsultarArtefactosEntregableUseCase {

    private final ArtefactoEntregableQueryOutputPort artefactoEntregableQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<ArtefactoEntregableReadModel> ejecutar(ArtefactoEntregableCriteria entrada) {
        logger.debug(ArtefactoEntregableKey.LOG_CONSULTANDO, entrada.entregableProyectoGrado());

        var artefactos = artefactoEntregableQueryOutputPort.consultarPorEntregable(entrada.entregableProyectoGrado());

        logger.debug(ArtefactoEntregableKey.LOG_CONSULTA_COMPLETADA,
                entrada.entregableProyectoGrado(), artefactos.size());
        return artefactos;
    }
}
