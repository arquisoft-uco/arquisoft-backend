package com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.EstadoEnEvaluacionExisteFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.EvaluacionFichaExisteFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.RepresentantePropietarioEvaluacionFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.UltimoEstadoEvaluacionFichaFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.AgregarEstadoEvaluacionFichaUseCase;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.AgregarEstadoEvaluacionFichaValidator;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.mapper.EstadoEvaluacionFichaMapper;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarEstadoEvaluacionFichaUseCaseImpl implements AgregarEstadoEvaluacionFichaUseCase {

    private final EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;
    private final EvaluacionFichaExisteFinder evaluacionFichaExisteFinder;
    private final RepresentantePropietarioEvaluacionFinder representantePropietarioEvaluacionFinder;
    private final EstadoEnEvaluacionExisteFinder estadoEnEvaluacionExisteFinder;
    private final UltimoEstadoEvaluacionFichaFinder ultimoEstadoEvaluacionFichaFinder;
    private final AgregarEstadoEvaluacionFichaValidator agregarEstadoEvaluacionFichaValidator;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(AgregacionEstadoEvaluacionFichaDomain entrada) {
        boolean evaluacionExiste = evaluacionFichaExisteFinder.obtener(entrada.getEvaluacionFichaPerfil());
        boolean esPropietario = representantePropietarioEvaluacionFinder.obtener(entrada);
        boolean estadoYaExiste = estadoEnEvaluacionExisteFinder.obtener(entrada);
        var ultimoEstado = ultimoEstadoEvaluacionFichaFinder.obtener(entrada.getEvaluacionFichaPerfil())
                .orElse(EstadoEvaluacionFichaDomain.VACIO);

        agregarEstadoEvaluacionFichaValidator.validar(
                entrada, evaluacionExiste, esPropietario, estadoYaExiste, ultimoEstado);

        var estadoEvaluacion = entrada.getEstadoEvaluacionFicha();

        estadoEvaluacionFichaOutputPort.agregarEstado(EstadoEvaluacionFichaMapper.toEntity(estadoEvaluacion));

        logger.info(
                Mensajes.obtener(EstadoEvaluacionFichaKey.LOG_AGREGADO),
                estadoEvaluacion.getId(),
                estadoEvaluacion.getEvaluacionFichaPerfilId(),
                estadoEvaluacion.getEstadoEvaluacion());

        return estadoEvaluacion.getId();
    }
}
