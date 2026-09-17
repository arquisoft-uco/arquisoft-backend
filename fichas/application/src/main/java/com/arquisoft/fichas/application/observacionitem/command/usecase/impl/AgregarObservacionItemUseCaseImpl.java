package com.arquisoft.fichas.application.observacionitem.command.usecase.impl;

import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.finder.FichaPerfilDelItemFinder;
import com.arquisoft.fichas.application.observacionitem.command.finder.ObservacionesIgualesEnRevisionFinder;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.mapper.ObservacionItemMapper;
import com.arquisoft.fichas.application.observacionitem.command.usecase.AgregarObservacionItemUseCase;
import com.arquisoft.fichas.application.observacionitem.command.validator.AgregarObservacionItemValidator;
import com.arquisoft.fichas.application.revisionitem.command.finder.RevisionItemFinder;
import com.arquisoft.fichas.domain.observacionitem.AgregacionObservacionItemDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionItemKey;
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
    private final AppLogger logger;

    @Override
    public UUID ejecutar(AgregacionObservacionItemDomain entrada) {
        logger.info(ObservacionItemKey.LOG_AGREGANDO, entrada.getRevisionItem());

        var revisionItem = revisionItemFinder.obtener(entrada.getRevisionItem());
        var revisionExiste = !revisionItem.esVacio();
        var estadoRevisionId = revisionItem.getEstadoRevision().getId();
        var item = revisionItem.getItem();

        var fichaPerfil = fichaPerfilDelItemFinder.obtener(item);
        var asesorDeLaFicha = fichaPerfilFinder.obtener(fichaPerfil).getAsesorFicha();

        var observacionesIguales = observacionesIgualesEnRevisionFinder.obtener(entrada);

        logger.debug(ObservacionItemKey.LOG_VERIFICACION_AGREGAR,
                revisionExiste, estadoRevisionId, asesorDeLaFicha, observacionesIguales);

        agregarObservacionItemValidator.validar(
                entrada, revisionExiste, estadoRevisionId, fichaPerfil, asesorDeLaFicha, observacionesIguales);

        var observacionItem = entrada.getObservacionItem();
        observacionItemOutputPort.registrarObservacion(ObservacionItemMapper.toEntity(observacionItem));

        logger.info(ObservacionItemKey.LOG_AGREGADA, observacionItem.getId(), observacionItem.getRevisionItem());
        return observacionItem.getId();
    }
}
