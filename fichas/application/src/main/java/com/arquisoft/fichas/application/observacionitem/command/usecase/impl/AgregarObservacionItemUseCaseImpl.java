package com.arquisoft.fichas.application.observacionitem.command.usecase.impl;

import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.finder.FichaPerfilDelItemFinder;
import com.arquisoft.fichas.application.observacionitem.command.finder.ObservacionesIgualesEnRevisionFinder;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.mapper.ObservacionItemMapper;
import com.arquisoft.fichas.application.observacionitem.command.usecase.AgregarObservacionItemUseCase;
import com.arquisoft.fichas.application.observacionitem.command.validator.AgregarObservacionItemValidator;
import com.arquisoft.fichas.application.revisionitem.command.finder.RevisionItemFinder;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.RevisionItemEntity;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.observacionitem.AgregacionObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.event.ObservacionItemAgregadaEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionItemKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarObservacionItemUseCaseImpl implements AgregarObservacionItemUseCase {

    private final RevisionItemFinder revisionItemFinder;
    private final FichaPerfilDelItemFinder fichaPerfilDelItemFinder;
    private final FichaPerfilFinder fichaPerfilFinder;
    private final ObservacionesIgualesEnRevisionFinder observacionesIgualesEnRevisionFinder;
    private final AgregarObservacionItemValidator agregarObservacionItemValidator;
    private final ObservacionItemOutputPort observacionItemOutputPort;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(AgregacionObservacionItemDomain entrada) {
        logger.info(ObservacionItemKey.LOG_AGREGANDO, entrada.getRevisionItem());

        var revisionItemOpt = revisionItemFinder.obtener(entrada.getRevisionItem());
        var revisionExiste = revisionItemOpt.isPresent();
        var estadoRevisionId = revisionItemOpt.map(RevisionItemEntity::estadoRevision)
                .orElse(UtilTexto.VACIO);
        var item = revisionItemOpt.map(RevisionItemEntity::item)
                .orElse(UtilUUID.obtenerUUIDPorDefecto());

        var fichaPerfil = fichaPerfilDelItemFinder.obtener(item)
                .orElse(UtilUUID.obtenerUUIDPorDefecto());
        var asesorDeLaFicha = fichaPerfilFinder.obtener(fichaPerfil)
                .map(FichaPerfilDomain::getAsesorFicha)
                .orElse(UtilUUID.obtenerUUIDPorDefecto());

        var observacionesIguales = observacionesIgualesEnRevisionFinder.obtener(entrada);

        logger.debug(ObservacionItemKey.LOG_VERIFICACION_AGREGAR,
                revisionExiste, estadoRevisionId, asesorDeLaFicha, observacionesIguales);

        agregarObservacionItemValidator.validar(
                entrada, revisionExiste, estadoRevisionId, fichaPerfil, asesorDeLaFicha, observacionesIguales);

        var observacionItem = entrada.getObservacionItem();
        observacionItemOutputPort.registrarObservacion(ObservacionItemMapper.toEntity(observacionItem));

        eventPublisher.publish(new ObservacionItemAgregadaEvent(
                observacionItem.getId(),
                observacionItem.getRevisionItem(),
                observacionItem.getObservacion(),
                observacionItem.getEstadoObservacionRevision().getId(),
                observacionItem.getEstadoObservacionRevision().getNombre()));

        logger.info(ObservacionItemKey.LOG_AGREGADA, observacionItem.getId(), observacionItem.getRevisionItem());
        return observacionItem.getId();
    }
}
