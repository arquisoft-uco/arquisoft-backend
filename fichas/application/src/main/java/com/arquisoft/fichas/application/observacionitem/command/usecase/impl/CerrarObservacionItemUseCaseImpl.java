package com.arquisoft.fichas.application.observacionitem.command.usecase.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.ContactosDeFichaFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.application.observacionitem.command.finder.ContextoObservacionItemFinder;
import com.arquisoft.fichas.application.observacionitem.command.finder.ObservacionesSinCerrarEnRevisionFinder;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.application.observacionitem.command.usecase.CerrarObservacionItemUseCase;
import com.arquisoft.fichas.application.observacionitem.command.validator.CerrarObservacionItemValidator;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.domain.estadoobservacionrevision.EstadoObservacionRevision;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.observacionitem.CierreObservacionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.event.RevisionItemCerradaEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionItemKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CerrarObservacionItemUseCaseImpl implements CerrarObservacionItemUseCase {

    private final ContextoObservacionItemFinder contextoObservacionItemFinder;
    private final ObservacionesSinCerrarEnRevisionFinder observacionesSinCerrarEnRevisionFinder;
    private final FichaPerfilFinder fichaPerfilFinder;
    private final ContactosDeFichaFinder contactosDeFichaFinder;
    private final CerrarObservacionItemValidator cerrarObservacionItemValidator;
    private final ObservacionItemOutputPort observacionItemOutputPort;
    private final RevisionItemOutputPort revisionItemOutputPort;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(CierreObservacionItemDomain entrada) {
        logger.info(ObservacionItemKey.LOG_CERRANDO, entrada.getObservacionItem(), entrada.getAsesorFicha());

        var contexto = contextoObservacionItemFinder.obtener(entrada.getObservacionItem());
        var otrasSinCerrar = observacionesSinCerrarEnRevisionFinder.obtener(entrada.getObservacionItem());
        var ficha = fichaPerfilFinder.obtener(contexto.fichaPerfil());
        var estudiantes = contactosDeFichaFinder.obtener(contexto.fichaPerfil());

        logger.debug(ObservacionItemKey.LOG_VERIFICACION_CERRAR,
                !contexto.esVacio(), contexto.estadoObservacion().getId(), contexto.estadoRevision().getId(),
                contexto.asesorFicha(), otrasSinCerrar, estudiantes.size());

        cerrarObservacionItemValidator.validar(entrada, contexto, ficha);

        observacionItemOutputPort.actualizarEstado(entrada.getObservacionItem(),
                EstadoObservacionRevision.EN_PROGRESO.getId(), EstadoObservacionRevision.CERRADO.getId());

        if (otrasSinCerrar == 0) {
            revisionItemOutputPort.actualizarEstado(contexto.revisionItem(),
                    EstadoRevision.CORRECCION_DISPONIBLE.getId(), EstadoRevision.CERRADA.getId());

            eventPublisher.publish(new RevisionItemCerradaEvent(
                    contexto.revisionItem(), ficha.getId(), ficha.getTituloProyecto(), estudiantes));
        }

        logger.info(ObservacionItemKey.LOG_CERRADA, entrada.getObservacionItem());
    }
}
