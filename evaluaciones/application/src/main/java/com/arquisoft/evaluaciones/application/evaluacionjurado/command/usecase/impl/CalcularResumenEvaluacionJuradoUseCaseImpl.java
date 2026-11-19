package com.arquisoft.evaluaciones.application.evaluacionjurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.mapper.ResumenEvaluacionMapper;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.ItemsCualitativosCalificadosJuradoFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.ItemsCuantitativosCalificadosJuradoFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.usecase.CalcularResumenEvaluacionJuradoUseCase;
import com.arquisoft.evaluaciones.domain.evaluacion.ResumenEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.CompletitudEvaluacionJuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CalcularResumenEvaluacionJuradoUseCaseImpl implements CalcularResumenEvaluacionJuradoUseCase {

    private final ItemsCualitativosCalificadosJuradoFinder itemsCualitativosCalificadosJuradoFinder;
    private final ItemsCuantitativosCalificadosJuradoFinder itemsCuantitativosCalificadosJuradoFinder;
    private final EvaluacionJuradoOutputPort evaluacionJuradoOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(CompletitudEvaluacionJuradoDomain completitud) {
        var evaluacionJurado = completitud.getEvaluacionJurado();

        if (!completitud.isCompleta()) {
            evaluacionJuradoOutputPort.actualizarResumen(
                    evaluacionJurado, ResumenEvaluacionMapper.toEntity(ResumenEvaluacionDomain.VACIO));
            logger.debug(EvaluacionJuradoKey.LOG_RESUMEN_LIMPIADO, evaluacionJurado);
            return;
        }

        var cualitativos = itemsCualitativosCalificadosJuradoFinder.obtener(evaluacionJurado);
        var cuantitativos = itemsCuantitativosCalificadosJuradoFinder.obtener(evaluacionJurado);

        var resumen = ResumenEvaluacionDomain.calcular(cualitativos, cuantitativos);
        evaluacionJuradoOutputPort.actualizarResumen(evaluacionJurado, ResumenEvaluacionMapper.toEntity(resumen));

        logger.debug(EvaluacionJuradoKey.LOG_RESUMEN_CALCULADO, evaluacionJurado, resumen.getNotaAcumulada());
    }
}
