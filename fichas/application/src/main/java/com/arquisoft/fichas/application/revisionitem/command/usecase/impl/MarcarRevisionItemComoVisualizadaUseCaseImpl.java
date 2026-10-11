package com.arquisoft.fichas.application.revisionitem.command.usecase.impl;

import com.arquisoft.fichas.application.revisionitem.command.finder.PertenenciaRevisionItemFinder;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.usecase.MarcarRevisionItemComoVisualizadaUseCase;
import com.arquisoft.fichas.application.revisionitem.command.validator.MarcarRevisionItemComoVisualizadaValidator;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.revisionitem.VisualizacionRevisionItemDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarcarRevisionItemComoVisualizadaUseCaseImpl implements MarcarRevisionItemComoVisualizadaUseCase {

    private final PertenenciaRevisionItemFinder pertenenciaRevisionItemFinder;
    private final MarcarRevisionItemComoVisualizadaValidator marcarRevisionItemComoVisualizadaValidator;
    private final RevisionItemOutputPort revisionItemOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(VisualizacionRevisionItemDomain entrada) {
        logger.info(RevisionItemKey.LOG_VISUALIZANDO, entrada.getRevisionItem(), entrada.getEstudiante());

        var pertenencia = pertenenciaRevisionItemFinder.obtener(entrada);
        var revisionExiste = !pertenencia.esVacio();
        var estadoRevision = pertenencia.estadoRevision();

        logger.debug(RevisionItemKey.LOG_VERIFICACION_VISUALIZAR,
                revisionExiste, pertenencia.esPropietario(), estadoRevision.getId());

        marcarRevisionItemComoVisualizadaValidator.validar(entrada.getRevisionItem(), entrada.getEstudiante(),
                pertenencia.fichaPerfil(), revisionExiste, pertenencia.esPropietario(), estadoRevision.getId());

        if (estadoRevision != EstadoRevision.NUEVA) {
            logger.info(RevisionItemKey.LOG_YA_VISUALIZADA, entrada.getRevisionItem(), estadoRevision.getId());
            return;
        }

        revisionItemOutputPort.actualizarEstado(entrada.getRevisionItem(),
                EstadoRevision.NUEVA.getId(), EstadoRevision.VISUALIZADA.getId());

        logger.info(RevisionItemKey.LOG_VISUALIZADA, entrada.getRevisionItem());
    }
}
