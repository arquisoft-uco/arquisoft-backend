package com.arquisoft.fichas.application.revisionitem.command.usecase.impl;

import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.finder.FichaPerfilDelItemFinder;
import com.arquisoft.fichas.application.revisionitem.command.finder.RevisionesDelItemFinder;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.usecase.ModificarRevisionItemUseCase;
import com.arquisoft.fichas.application.revisionitem.command.validator.ModificarRevisionItemValidator;
import com.arquisoft.fichas.domain.revisionitem.ModificacionRevisionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.event.RevisionItemModificadoEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ModificarRevisionItemUseCaseImpl implements ModificarRevisionItemUseCase {

    private final RevisionesDelItemFinder revisionesDelItemFinder;
    private final FichaPerfilDelItemFinder fichaPerfilDelItemFinder;
    private final FichaPerfilFinder fichaPerfilFinder;
    private final ModificarRevisionItemValidator modificarRevisionItemValidator;
    private final RevisionItemOutputPort revisionItemOutputPort;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificacionRevisionItemDomain entrada) {
        boolean revisionExiste = revisionesDelItemFinder.obtener(entrada.getItem()) > 0;

        var fichaPerfilDelItem = fichaPerfilDelItemFinder.obtener(entrada.getItem());
        UUID fichaPerfil = fichaPerfilDelItem.orElse(UtilUUID.obtenerUUIDPorDefecto());

        boolean esPropietario = fichaPerfilDelItem
                .flatMap(fichaPerfilFinder::obtener)
                .map(ficha -> ficha.getAsesorFicha().equals(entrada.getAsesorFicha()))
                .orElse(false);

        modificarRevisionItemValidator.validar(entrada, revisionExiste, fichaPerfil, esPropietario);

        revisionItemOutputPort.actualizarEstado(entrada.getItem(), entrada.getEstadoRevision().getId());

        eventPublisher.publish(new RevisionItemModificadoEvent(
                entrada.getItem(), entrada.getEstadoRevision().getId(), entrada.getEstadoRevision().getNombre()));

        logger.info(Mensajes.obtener(RevisionItemKey.LOG_MODIFICADO), entrada.getItem());
    }
}
