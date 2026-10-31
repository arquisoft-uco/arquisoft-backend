package com.arquisoft.artefactos.application.observacion.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorObservacionFinder;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import com.arquisoft.artefactos.application.observacion.command.usecase.EliminarObservacionUseCase;
import com.arquisoft.artefactos.application.observacion.command.validator.EliminarObservacionValidator;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAsesorPorObservacionFinder;
import com.arquisoft.artefactos.domain.observacion.EliminacionObservacionDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EliminarObservacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EliminarObservacionUseCaseImpl implements EliminarObservacionUseCase {

    private final RevisionAsesorPorObservacionFinder revisionAsesorPorObservacionFinder;
    private final ArtefactoPorObservacionFinder artefactoPorObservacionFinder;
    private final EliminarObservacionValidator eliminarObservacionValidator;
    private final ObservacionOutputPort observacionOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(EliminacionObservacionDomain eliminacion) {
        var observacion = eliminacion.getObservacion();

        logger.info(EliminarObservacionKey.LOG_ELIMINANDO, observacion, eliminacion.getAsesor());

        var revision = revisionAsesorPorObservacionFinder.obtener(observacion);
        var artefacto = artefactoPorObservacionFinder.obtener(observacion);

        logger.debug(EliminarObservacionKey.LOG_VERIFICACION_ELIMINAR,
                !revision.esVacio(), !artefacto.esVacio(), revision.getEstado().getId());

        eliminarObservacionValidator.validar(eliminacion, revision, artefacto);

        observacionOutputPort.eliminar(observacion);

        logger.info(EliminarObservacionKey.LOG_ELIMINADA, observacion);
    }
}
