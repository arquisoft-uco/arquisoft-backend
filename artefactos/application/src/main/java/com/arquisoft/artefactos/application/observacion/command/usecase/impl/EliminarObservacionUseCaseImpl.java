package com.arquisoft.artefactos.application.observacion.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorObservacionFinder;
import com.arquisoft.artefactos.application.observacion.command.finder.ObservacionPorIdFinder;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import com.arquisoft.artefactos.application.observacion.command.usecase.EliminarObservacionUseCase;
import com.arquisoft.artefactos.application.observacion.command.validator.EliminarObservacionValidator;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAsesorPorObservacionFinder;
import com.arquisoft.artefactos.domain.observacion.EliminacionObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EliminarObservacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EliminarObservacionUseCaseImpl implements EliminarObservacionUseCase {

    private final RevisionAsesorPorObservacionFinder revisionAsesorPorObservacionFinder;
    private final ObservacionPorIdFinder observacionPorIdFinder;
    private final ArtefactoPorObservacionFinder artefactoPorObservacionFinder;
    private final EliminarObservacionValidator eliminarObservacionValidator;
    private final ObservacionOutputPort observacionOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(EliminacionObservacionDomain eliminacion) {
        var observacionId = eliminacion.getObservacion();

        logger.info(EliminarObservacionKey.LOG_ELIMINANDO, observacionId, eliminacion.getAsesor());

        var revision = revisionAsesorPorObservacionFinder.obtener(observacionId);
        var observacion = observacionPorIdFinder.obtener(observacionId);
        var artefacto = artefactoPorObservacionFinder.obtener(observacionId);

        logger.debug(EliminarObservacionKey.LOG_VERIFICACION_ELIMINAR,
                !observacion.esVacio(), !revision.esVacio(), !artefacto.esVacio(), revision.getEstado().getId(),
                observacion.esContinuacion());

        eliminarObservacionValidator.validar(eliminacion, observacion, revision, artefacto);

        observacionOutputPort.eliminar(observacionId);

        if (observacion.esContinuacion()) {
            observacionOutputPort.devolverEstado(observacion.getObservacionOrigen(),
                    EstadoObservacion.REABIERTA.getId(), EstadoObservacion.SOLUCIONADA.getId());
        }

        logger.info(EliminarObservacionKey.LOG_ELIMINADA, observacionId);
    }
}
