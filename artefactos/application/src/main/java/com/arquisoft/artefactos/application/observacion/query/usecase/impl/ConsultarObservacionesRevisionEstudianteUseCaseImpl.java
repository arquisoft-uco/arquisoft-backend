package com.arquisoft.artefactos.application.observacion.query.usecase.impl;

import com.arquisoft.artefactos.application.observacion.query.criteria.ObservacionEstudianteCriteria;
import com.arquisoft.artefactos.application.observacion.query.readmodel.ObservacionReadModel;
import com.arquisoft.artefactos.application.observacion.query.secondaryport.ObservacionQueryOutputPort;
import com.arquisoft.artefactos.application.observacion.query.usecase.ConsultarObservacionesRevisionEstudianteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ObservacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarObservacionesRevisionEstudianteUseCaseImpl
        implements ConsultarObservacionesRevisionEstudianteUseCase {

    private final ObservacionQueryOutputPort observacionQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<ObservacionReadModel> ejecutar(ObservacionEstudianteCriteria entrada) {
        logger.debug(ObservacionKey.LOG_CONSULTANDO, entrada.revisionAsesor());

        var observaciones = observacionQueryOutputPort.consultarDeEstudiante(
                entrada.revisionAsesor(), entrada.estudiante());

        logger.debug(ObservacionKey.LOG_CONSULTA_COMPLETADA, observaciones.size());
        return observaciones;
    }
}
