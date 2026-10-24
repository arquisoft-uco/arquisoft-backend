package com.arquisoft.artefactos.application.observacion.query.usecase.impl;

import com.arquisoft.artefactos.application.observacion.query.criteria.ObservacionCriteria;
import com.arquisoft.artefactos.application.observacion.query.readmodel.ObservacionReadModel;
import com.arquisoft.artefactos.application.observacion.query.secondaryport.ObservacionQueryOutputPort;
import com.arquisoft.artefactos.application.observacion.query.usecase.ConsultarObservacionesRevisionUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ObservacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarObservacionesRevisionUseCaseImpl implements ConsultarObservacionesRevisionUseCase {

    private final ObservacionQueryOutputPort observacionQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<ObservacionReadModel> ejecutar(ObservacionCriteria entrada) {
        logger.debug(ObservacionKey.LOG_CONSULTANDO, entrada.revisionAsesor());

        var observaciones = observacionQueryOutputPort.consultarPorRevisionAsesor(entrada.revisionAsesor());

        logger.debug(ObservacionKey.LOG_CONSULTA_COMPLETADA, observaciones.size());
        return observaciones;
    }
}
