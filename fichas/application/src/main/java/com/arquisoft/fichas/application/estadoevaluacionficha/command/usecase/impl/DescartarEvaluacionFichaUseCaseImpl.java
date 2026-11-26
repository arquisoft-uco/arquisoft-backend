package com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.EstadoFichaDeEvaluacionFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.EvaluacionFichaExisteFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.RepresentantePropietarioEvaluacionFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.UltimoEstadoEvaluacionFichaFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.mapper.EstadoEvaluacionFichaMapper;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.DescartarEvaluacionFichaUseCase;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.DescartarEvaluacionFichaValidator;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DescartarEvaluacionFichaUseCaseImpl implements DescartarEvaluacionFichaUseCase {

    private final EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;
    private final EvaluacionFichaExisteFinder evaluacionFichaExisteFinder;
    private final RepresentantePropietarioEvaluacionFinder representantePropietarioEvaluacionFinder;
    private final UltimoEstadoEvaluacionFichaFinder ultimoEstadoEvaluacionFichaFinder;
    private final EstadoFichaDeEvaluacionFinder estadoFichaDeEvaluacionFinder;
    private final DescartarEvaluacionFichaValidator descartarEvaluacionFichaValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(AgregacionEstadoEvaluacionFichaDomain entrada) {
        logger.info(EstadoEvaluacionFichaKey.LOG_DESCARTANDO,
                entrada.getEvaluacionFichaPerfil(), entrada.getRepresentanteComite());

        var evaluacionExiste = evaluacionFichaExisteFinder.obtener(entrada.getEvaluacionFichaPerfil());
        var esPropietario = representantePropietarioEvaluacionFinder.obtener(entrada);
        var ultimoEstado = ultimoEstadoEvaluacionFichaFinder.obtener(entrada.getEvaluacionFichaPerfil());
        var estadoFicha = estadoFichaDeEvaluacionFinder.obtener(entrada.getEvaluacionFichaPerfil());

        logger.debug(EstadoEvaluacionFichaKey.LOG_VERIFICACION_DESCARTAR,
                evaluacionExiste, esPropietario,
                ultimoEstado.getEstadoEvaluacion().getId(), estadoFicha.estadoActual().getId());

        descartarEvaluacionFichaValidator.validar(entrada, evaluacionExiste, esPropietario, ultimoEstado, estadoFicha);

        var estadoEvaluacion = entrada.getEstadoEvaluacionFicha();

        estadoEvaluacionFichaOutputPort.agregarEstado(EstadoEvaluacionFichaMapper.toEntity(estadoEvaluacion));

        logger.info(EstadoEvaluacionFichaKey.LOG_DESCARTADA,
                estadoEvaluacion.getEvaluacionFichaPerfilId(), estadoEvaluacion.getEstadoEvaluacion());
    }
}
