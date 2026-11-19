package com.arquisoft.fichas.application.observacionitem.command.usecase.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.ContactosDeFichaFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.application.observacionitem.command.finder.ContextoObservacionItemFinder;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.application.observacionitem.command.usecase.ReabrirObservacionItemUseCase;
import com.arquisoft.fichas.application.observacionitem.command.validator.ReabrirObservacionItemValidator;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.domain.estadoobservacionrevision.EstadoObservacionRevision;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.observacionitem.ReaperturaObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.event.ObservacionItemReabiertaEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionItemKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReabrirObservacionItemUseCaseImpl implements ReabrirObservacionItemUseCase {

    private final ContextoObservacionItemFinder contextoObservacionItemFinder;
    private final FichaPerfilFinder fichaPerfilFinder;
    private final ContactosDeFichaFinder contactosDeFichaFinder;
    private final ReabrirObservacionItemValidator reabrirObservacionItemValidator;
    private final ObservacionItemOutputPort observacionItemOutputPort;
    private final RevisionItemOutputPort revisionItemOutputPort;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(ReaperturaObservacionItemDomain entrada) {
        logger.info(ObservacionItemKey.LOG_REABRIENDO, entrada.getObservacionItem(), entrada.getAsesorFicha());

        var contexto = contextoObservacionItemFinder.obtener(entrada.getObservacionItem());
        var ficha = fichaPerfilFinder.obtener(contexto.fichaPerfil());
        var estudiantes = contactosDeFichaFinder.obtener(contexto.fichaPerfil());

        logger.debug(ObservacionItemKey.LOG_VERIFICACION_REABRIR,
                !contexto.esVacio(), contexto.estadoObservacion().getId(), contexto.estadoRevision().getId(),
                contexto.asesorFicha(), estudiantes.size());

        reabrirObservacionItemValidator.validar(entrada, contexto, ficha);

        observacionItemOutputPort.actualizarEstado(entrada.getObservacionItem(),
                EstadoObservacionRevision.CERRADO.getId(), EstadoObservacionRevision.EN_PROGRESO.getId());

        revisionItemOutputPort.actualizarEstado(contexto.revisionItem(),
                EstadoRevision.CORRECCION_DISPONIBLE.getId(), EstadoRevision.EN_PROGRESO.getId());

        eventPublisher.publish(new ObservacionItemReabiertaEvent(
                entrada.getObservacionItem(), contexto.revisionItem(), ficha.getId(),
                ficha.getTituloProyecto(), estudiantes));

        logger.info(ObservacionItemKey.LOG_REABIERTA, entrada.getObservacionItem());
    }
}
