package com.arquisoft.evaluaciones.application.evaluacionasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.mapper.ResumenEvaluacionMapper;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.ItemsCualitativosCalificadosAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.ItemsCuantitativosCalificadosAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.EvaluacionAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.usecase.CalcularResumenEvaluacionAsesorUseCase;
import com.arquisoft.evaluaciones.domain.evaluacion.ResumenEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.CompletitudEvaluacionAsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CalcularResumenEvaluacionAsesorUseCaseImpl implements CalcularResumenEvaluacionAsesorUseCase {

    private final ItemsCualitativosCalificadosAsesorFinder itemsCualitativosCalificadosAsesorFinder;
    private final ItemsCuantitativosCalificadosAsesorFinder itemsCuantitativosCalificadosAsesorFinder;
    private final EvaluacionAsesorOutputPort evaluacionAsesorOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(CompletitudEvaluacionAsesorDomain completitud) {
        var evaluacionAsesor = completitud.getEvaluacionAsesor();

        if (!completitud.isCompleta()) {
            evaluacionAsesorOutputPort.actualizarResumen(
                    evaluacionAsesor, ResumenEvaluacionMapper.toEntity(ResumenEvaluacionDomain.VACIO));
            logger.debug(EvaluacionAsesorKey.LOG_RESUMEN_LIMPIADO, evaluacionAsesor);
            return;
        }

        var cualitativos = itemsCualitativosCalificadosAsesorFinder.obtener(evaluacionAsesor);
        var cuantitativos = itemsCuantitativosCalificadosAsesorFinder.obtener(evaluacionAsesor);

        var resumen = ResumenEvaluacionDomain.calcular(cualitativos, cuantitativos);
        evaluacionAsesorOutputPort.actualizarResumen(evaluacionAsesor, ResumenEvaluacionMapper.toEntity(resumen));

        logger.debug(EvaluacionAsesorKey.LOG_RESUMEN_CALCULADO, evaluacionAsesor, resumen.getNotaAcumulada());
    }
}
