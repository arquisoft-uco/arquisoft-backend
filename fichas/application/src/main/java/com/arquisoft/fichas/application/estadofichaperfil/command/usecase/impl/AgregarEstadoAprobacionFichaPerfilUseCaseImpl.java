package com.arquisoft.fichas.application.estadofichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorDeFichaFinder;
import com.arquisoft.fichas.application.estadofichaperfil.command.finder.EstadoActualFichaPerfilFinder;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.mapper.EstadoFichaPerfilMapper;
import com.arquisoft.fichas.application.estadofichaperfil.command.usecase.AgregarEstadoAprobacionFichaPerfilUseCase;
import com.arquisoft.fichas.application.estadofichaperfil.command.validator.AgregarEstadoAprobacionFichaPerfilValidator;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.IntegrantesVigentesDeFichaFinder;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.finder.ResumenEvaluacionesFichaFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.fichas.domain.asesorficha.model.ContactoAsesor;
import com.arquisoft.fichas.domain.estadofichaperfil.DecisionFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.event.FichaPerfilAprobadaEvent;
import com.arquisoft.fichas.domain.estadofichaperfil.event.FichaPerfilNoAprobadaEvent;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.IntegranteFicha;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarEstadoAprobacionFichaPerfilUseCaseImpl implements AgregarEstadoAprobacionFichaPerfilUseCase {

    private final EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort;
    private final FichaPerfilFinder fichaPerfilFinder;
    private final EstadoActualFichaPerfilFinder estadoActualFichaPerfilFinder;
    private final IntegrantesVigentesDeFichaFinder integrantesVigentesDeFichaFinder;
    private final ResumenEvaluacionesFichaFinder resumenEvaluacionesFichaFinder;
    private final AsesorDeFichaFinder asesorDeFichaFinder;
    private final AgregarEstadoAprobacionFichaPerfilValidator validator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(DecisionFichaPerfilDomain decision) {
        logger.info(EstadoFichaPerfilKey.LOG_AGREGANDO_APROBACION, decision.getFichaPerfil(), decision.isAcepta());

        var ficha = fichaPerfilFinder.obtener(decision.getFichaPerfil());
        var estadoActual = estadoActualFichaPerfilFinder.obtener(decision.getFichaPerfil());
        var integrantes = integrantesVigentesDeFichaFinder.obtener(decision.getFichaPerfil());
        var resumen = resumenEvaluacionesFichaFinder.obtener(decision.getFichaPerfil());
        var asesor = asesorDeFichaFinder.obtener(decision.getFichaPerfil());

        logger.debug(EstadoFichaPerfilKey.LOG_VERIFICACION_APROBACION,
                !ficha.esVacio(), !asesor.esVacio(), estadoActual.getEstadoFicha(), integrantes.size(),
                resumen.finalizadas(), resumen.aprobatorias(), resumen.tieneObservacionesVigentes());

        validator.validar(decision, ficha, asesor, estadoActual, resumen, integrantes);

        var nuevoEstado = EstadoFichaPerfilDomain.crearPorDecision(
                decision.getFichaPerfil(), decision.isAcepta(), resumen);

        estadoFichaPerfilOutputPort.agregarEstado(EstadoFichaPerfilMapper.toEntity(nuevoEstado));

        eventPublisher.publish(eventoDeDecision(nuevoEstado, ficha, decision.getCoordinador(), asesor, integrantes));

        logger.info(EstadoFichaPerfilKey.LOG_APROBACION_AGREGADA,
                nuevoEstado.getId(), nuevoEstado.getFichaPerfil(), nuevoEstado.getEstadoFicha());

        return nuevoEstado.getId();
    }

    private static DomainEvent eventoDeDecision(EstadoFichaPerfilDomain estado, FichaPerfilDomain ficha,
                                                UUID coordinador, AsesorFichaDomain asesor,
                                                List<IntegranteFicha> integrantes) {
        var contactoAsesor = new ContactoAsesor(asesor.getNombre(), asesor.getEmail());
        return estado.getEstadoFicha().esAprobatorio()
                ? new FichaPerfilAprobadaEvent(ficha.getId(), ficha.getTituloProyecto(),
                        estado.getEstadoFicha().getId(), coordinador, contactoAsesor, integrantes)
                : new FichaPerfilNoAprobadaEvent(ficha.getId(), ficha.getTituloProyecto(), contactoAsesor,
                        integrantes.stream().map(IntegranteFicha::contacto).toList());
    }
}
