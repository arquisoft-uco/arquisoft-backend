package com.arquisoft.fichas.application.revisionitem.command.usecase.impl;

import com.arquisoft.fichas.application.revisionitem.command.finder.AsesoriaRevisionItemFinder;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.usecase.RemoverRevisionItemUseCase;
import com.arquisoft.fichas.application.revisionitem.command.validator.RemoverRevisionItemValidator;
import com.arquisoft.fichas.domain.revisionitem.RemocionRevisionItemDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverRevisionItemUseCaseImpl implements RemoverRevisionItemUseCase {

    private final AsesoriaRevisionItemFinder asesoriaRevisionItemFinder;
    private final RemoverRevisionItemValidator removerRevisionItemValidator;
    private final RevisionItemOutputPort revisionItemOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemocionRevisionItemDomain entrada) {
        logger.info(RevisionItemKey.LOG_REMOVIENDO, entrada.getRevisionItem(), entrada.getAsesorFicha());

        var asesoria = asesoriaRevisionItemFinder.obtener(entrada.getRevisionItem());
        var revisionExiste = !asesoria.esVacio();
        var estadoRevision = asesoria.estadoRevision();

        logger.debug(RevisionItemKey.LOG_VERIFICACION_REMOVER,
                revisionExiste, asesoria.asesorFicha(), estadoRevision.getId());

        removerRevisionItemValidator.validar(entrada.getRevisionItem(), entrada.getAsesorFicha(),
                asesoria.fichaPerfil(), asesoria.asesorFicha(), revisionExiste, estadoRevision.getId());

        revisionItemOutputPort.removerRevision(entrada.getRevisionItem());

        logger.info(RevisionItemKey.LOG_REMOVIDA, entrada.getRevisionItem());
    }
}
