package com.arquisoft.fichas.application.estadofichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.estadofichaperfil.command.finder.EstadoActualFichaPerfilFinder;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.mapper.EstadoFichaPerfilMapper;
import com.arquisoft.fichas.application.estadofichaperfil.command.usecase.AgregarEstadoFichaPerfilUseCase;
import com.arquisoft.fichas.application.estadofichaperfil.command.validator.AgregarEstadoFichaPerfilValidator;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.IntegrantesVigentesDeFichaFinder;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.finder.ResumenEvaluacionesFichaFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.domain.estadofichaperfil.AgregacionEstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.event.EstadoFichaPerfilAgregadoEvent;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.IntegranteFicha;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarEstadoFichaPerfilUseCaseImpl implements AgregarEstadoFichaPerfilUseCase {

    private final EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort;
    private final FichaPerfilFinder fichaPerfilFinder;
    private final EstadoActualFichaPerfilFinder estadoActualFichaPerfilFinder;
    private final ResumenEvaluacionesFichaFinder resumenEvaluacionesFichaFinder;
    private final IntegrantesVigentesDeFichaFinder integrantesVigentesDeFichaFinder;
    private final AgregarEstadoFichaPerfilValidator validator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(AgregacionEstadoFichaPerfilDomain entrada) {
        var estado = entrada.getEstado();
        logger.info(EstadoFichaPerfilKey.LOG_AGREGANDO, entrada.getFichaPerfil(), estado.getEstadoFicha().getId());

        var ficha = fichaPerfilFinder.obtener(entrada.getFichaPerfil());
        var estadoActual = estadoActualFichaPerfilFinder.obtener(entrada.getFichaPerfil());
        var resumen = resumenEvaluacionesFichaFinder.obtener(entrada.getFichaPerfil());
        var integrantes = integrantesVigentesDeFichaFinder.obtener(entrada.getFichaPerfil());

        logger.debug(EstadoFichaPerfilKey.LOG_VERIFICACION_AGREGAR,
                !ficha.esVacio(), estadoActual.getEstadoFicha(), resumen.enEvaluacion(), integrantes.size());

        validator.validar(entrada, ficha, estadoActual, resumen, integrantes);

        estadoFichaPerfilOutputPort.agregarEstado(EstadoFichaPerfilMapper.toEntity(estado));

        eventPublisher.publish(new EstadoFichaPerfilAgregadoEvent(
                estado.getId(),
                ficha.getId(),
                ficha.getTituloProyecto(),
                estado.getEstadoFicha().getId(),
                estado.getEstadoFicha().getNombre(),
                integrantes.stream().map(IntegranteFicha::contacto).toList()));

        logger.info(EstadoFichaPerfilKey.LOG_AGREGADO,
                estado.getId(), estado.getFichaPerfil(), estado.getEstadoFicha().getId());

        return estado.getId();
    }
}
