package com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.EstadoEnEvaluacionExisteFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.EstadoFichaDeEvaluacionFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.EvaluacionFichaExisteFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.ObservacionesEvaluacionCountFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.RepresentantePropietarioEvaluacionFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.UltimoEstadoEvaluacionFichaFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.AgregarEstadoEvaluacionFichaUseCase;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.AgregarEstadoEvaluacionFichaValidator;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
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
    private final ObservacionesEvaluacionCountFinder observacionesEvaluacionCountFinder;
    private final EstadoFichaDeEvaluacionFinder estadoFichaDeEvaluacionFinder;
    private final AgregarEstadoEvaluacionFichaValidator agregarEstadoEvaluacionFichaValidator;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(AgregacionEstadoEvaluacionFichaDomain entrada) {
        logger.info(EstadoEvaluacionFichaKey.LOG_AGREGANDO,
                entrada.getEvaluacionFichaPerfil(),
                entrada.getEstadoEvaluacionFicha().getEstadoEvaluacion());

        var evaluacionExiste = evaluacionFichaExisteFinder.obtener(entrada.getEvaluacionFichaPerfil());
        var esPropietario = representantePropietarioEvaluacionFinder.obtener(entrada);
        var estadoYaExiste = estadoEnEvaluacionExisteFinder.obtener(entrada);
        var ultimoEstado = ultimoEstadoEvaluacionFichaFinder.obtener(entrada.getEvaluacionFichaPerfil());
        var observaciones = observacionesEvaluacionCountFinder.obtener(entrada.getEvaluacionFichaPerfil());
        var estadoFicha = estadoFichaDeEvaluacionFinder.obtener(entrada.getEvaluacionFichaPerfil());

        logger.debug(EstadoEvaluacionFichaKey.LOG_VERIFICACION_AGREGAR,
                evaluacionExiste, esPropietario, estadoYaExiste, observaciones, estadoFicha.estadoActual().getId());

        agregarEstadoEvaluacionFichaValidator.validar(
                entrada, evaluacionExiste, esPropietario, estadoYaExiste, ultimoEstado, observaciones, estadoFicha);

        var estadoEvaluacion = entrada.getEstadoEvaluacionFicha();

        estadoEvaluacionFichaOutputPort.agregarEstado(EstadoEvaluacionFichaMapper.toEntity(estadoEvaluacion));

        logger.info(
                EstadoEvaluacionFichaKey.LOG_AGREGADO,
                estadoEvaluacion.getId(),
                estadoEvaluacion.getEvaluacionFichaPerfilId(),
                estadoEvaluacion.getEstadoEvaluacion());

        return estadoEvaluacion.getId();
    }
}
