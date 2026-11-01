package com.arquisoft.artefactos.application.revisionasesor.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorRevisionAsesorFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAsesorFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import com.arquisoft.artefactos.application.revisionasesor.command.usecase.EliminarRevisionAsesorUseCase;
import com.arquisoft.artefactos.application.revisionasesor.command.validator.EliminarRevisionAsesorValidator;
import com.arquisoft.artefactos.domain.revisionasesor.EliminacionRevisionAsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EliminarRevisionAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EliminarRevisionAsesorUseCaseImpl implements EliminarRevisionAsesorUseCase {

    private final RevisionAsesorFinder revisionAsesorFinder;
    private final ArtefactoPorRevisionAsesorFinder artefactoPorRevisionAsesorFinder;
    private final EliminarRevisionAsesorValidator eliminarRevisionAsesorValidator;
    private final RevisionAsesorOutputPort revisionAsesorOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(EliminacionRevisionAsesorDomain eliminacion) {
        var revisionAsesor = eliminacion.getRevisionAsesor();

        logger.info(EliminarRevisionAsesorKey.LOG_ELIMINANDO, revisionAsesor, eliminacion.getAsesor());

        var revision = revisionAsesorFinder.obtener(revisionAsesor);
        var artefacto = artefactoPorRevisionAsesorFinder.obtener(revisionAsesor);

        logger.debug(EliminarRevisionAsesorKey.LOG_VERIFICACION_ELIMINAR,
                !revision.esVacio(), !artefacto.esVacio(), revision.getEstado().getId());

        eliminarRevisionAsesorValidator.validar(eliminacion, revision, artefacto);

        revisionAsesorOutputPort.eliminar(revisionAsesor);

        logger.info(EliminarRevisionAsesorKey.LOG_ELIMINADA, revisionAsesor);
    }
}
