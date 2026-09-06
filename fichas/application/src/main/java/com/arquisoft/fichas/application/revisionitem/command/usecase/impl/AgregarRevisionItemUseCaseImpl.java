package com.arquisoft.fichas.application.revisionitem.command.usecase.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.ContactosDeFichaFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.finder.FichaPerfilDelItemFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.finder.ItemFichaPerfilExisteFinder;
import com.arquisoft.fichas.application.revisionitem.command.finder.RevisionesDelItemFinder;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.mapper.RevisionItemMapper;
import com.arquisoft.fichas.application.revisionitem.command.usecase.AgregarRevisionItemUseCase;
import com.arquisoft.fichas.application.revisionitem.command.validator.AgregarRevisionItemValidator;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.revisionitem.AgregacionRevisionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.event.RevisionItemAgregadoEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarRevisionItemUseCaseImpl implements AgregarRevisionItemUseCase {

    private final ItemFichaPerfilExisteFinder itemFichaPerfilExisteFinder;
    private final FichaPerfilDelItemFinder fichaPerfilDelItemFinder;
    private final FichaPerfilFinder fichaPerfilFinder;
    private final RevisionesDelItemFinder revisionesDelItemFinder;
    private final ContactosDeFichaFinder contactosDeFichaFinder;
    private final AgregarRevisionItemValidator agregarRevisionItemValidator;
    private final RevisionItemOutputPort revisionItemOutputPort;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(AgregacionRevisionItemDomain entrada) {
        logger.info(RevisionItemKey.LOG_AGREGANDO,
                entrada.getItem(), entrada.getRevisionItem().getEstadoRevision().getId());

        boolean itemExiste = itemFichaPerfilExisteFinder.obtener(entrada.getItem());

        UUID fichaPerfil = fichaPerfilDelItemFinder.obtener(entrada.getItem())
                .orElse(UtilUUID.obtenerUUIDPorDefecto());

        var ficha = fichaPerfilFinder.obtener(fichaPerfil).orElse(FichaPerfilDomain.VACIO);

        long cantidadRevisiones = revisionesDelItemFinder.obtener(entrada.getItem());

        logger.debug(RevisionItemKey.LOG_VERIFICACION_AGREGAR,
                itemExiste, ficha.getAsesorFicha(), cantidadRevisiones);

        agregarRevisionItemValidator.validar(entrada, itemExiste, fichaPerfil, ficha.getAsesorFicha(),
                cantidadRevisiones);

        var revisionItem = entrada.getRevisionItem();
        revisionItemOutputPort.registrarRevision(RevisionItemMapper.toEntity(revisionItem));

        eventPublisher.publish(new RevisionItemAgregadoEvent(
                revisionItem.getId(), revisionItem.getItem(),
                revisionItem.getEstadoRevision().getId(), revisionItem.getEstadoRevision().getNombre(),
                revisionItem.getFechaCreacion(),
                ficha.getTituloProyecto(),
                contactosDeFichaFinder.obtener(fichaPerfil)));

        logger.info(RevisionItemKey.LOG_AGREGADO, revisionItem.getId(), revisionItem.getItem());

        return revisionItem.getId();
    }
}
