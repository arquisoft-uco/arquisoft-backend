package com.arquisoft.fichas.application.revisionitem.command.usecase.impl;

import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorDeFichaFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.application.observacionitem.command.finder.ObservacionesDeRevisionFinder;
import com.arquisoft.fichas.application.revisionitem.command.finder.PertenenciaRevisionItemFinder;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.usecase.EnviarCorreccionesRevisionItemUseCase;
import com.arquisoft.fichas.application.revisionitem.command.validator.EnviarCorreccionesRevisionItemValidator;
import com.arquisoft.fichas.domain.asesorficha.model.ContactoAsesor;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.revisionitem.EnvioCorreccionesRevisionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.event.CorreccionesRevisionItemEnviadasEvent;
import com.arquisoft.fichas.domain.revisionitem.model.RevisionItemDeEstudiante;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnviarCorreccionesRevisionItemUseCaseImpl implements EnviarCorreccionesRevisionItemUseCase {

    private final PertenenciaRevisionItemFinder pertenenciaRevisionItemFinder;
    private final FichaPerfilFinder fichaPerfilFinder;
    private final AsesorDeFichaFinder asesorDeFichaFinder;
    private final ObservacionesDeRevisionFinder observacionesDeRevisionFinder;
    private final EnviarCorreccionesRevisionItemValidator enviarCorreccionesRevisionItemValidator;
    private final RevisionItemOutputPort revisionItemOutputPort;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(EnvioCorreccionesRevisionItemDomain entrada) {
        logger.info(RevisionItemKey.LOG_ENVIANDO_CORRECCIONES, entrada.getRevisionItem(), entrada.getEstudiante());

        var pertenencia = pertenenciaRevisionItemFinder.obtener(
                new RevisionItemDeEstudiante(entrada.getRevisionItem(), entrada.getEstudiante()));
        var ficha = fichaPerfilFinder.obtener(pertenencia.fichaPerfil());
        var asesor = asesorDeFichaFinder.obtener(pertenencia.fichaPerfil());
        var observaciones = observacionesDeRevisionFinder.obtener(entrada.getRevisionItem());

        logger.debug(RevisionItemKey.LOG_VERIFICACION_ENVIAR_CORRECCIONES,
                !pertenencia.esVacio(), pertenencia.esPropietario(), pertenencia.estadoRevision().getId(),
                observaciones.total(), observaciones.pendientes());

        enviarCorreccionesRevisionItemValidator.validar(entrada, pertenencia, ficha, asesor, observaciones);

        revisionItemOutputPort.actualizarEstado(entrada.getRevisionItem(),
                EstadoRevision.EN_PROGRESO.getId(), EstadoRevision.CORRECCION_DISPONIBLE.getId());

        eventPublisher.publish(new CorreccionesRevisionItemEnviadasEvent(
                entrada.getRevisionItem(), ficha.getId(), ficha.getTituloProyecto(),
                new ContactoAsesor(asesor.getNombre(), asesor.getEmail())));

        logger.info(RevisionItemKey.LOG_CORRECCIONES_ENVIADAS, entrada.getRevisionItem());
    }
}
