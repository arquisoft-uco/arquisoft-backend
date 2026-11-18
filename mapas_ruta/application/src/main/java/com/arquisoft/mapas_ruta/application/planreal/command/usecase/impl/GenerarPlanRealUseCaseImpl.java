package com.arquisoft.mapas_ruta.application.planreal.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.finder.PlanEstimadoDeTareaFinder;
import com.arquisoft.mapas_ruta.application.planreal.command.finder.PlanRealDeTareaExisteFinder;
import com.arquisoft.mapas_ruta.application.planreal.command.result.GeneracionPlanRealResult;
import com.arquisoft.mapas_ruta.application.planreal.command.result.mapper.GeneracionPlanRealResultMapper;
import com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.PlanRealOutputPort;
import com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.mapper.PlanRealMapper;
import com.arquisoft.mapas_ruta.application.planreal.command.usecase.GenerarPlanRealUseCase;
import com.arquisoft.mapas_ruta.domain.planreal.GeneracionPlanRealDomain;
import com.arquisoft.mapas_ruta.domain.planreal.PlanRealDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.PlanRealKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenerarPlanRealUseCaseImpl implements GenerarPlanRealUseCase {

    private final PlanRealOutputPort planRealOutputPort;
    private final PlanEstimadoDeTareaFinder planEstimadoDeTareaFinder;
    private final PlanRealDeTareaExisteFinder planRealDeTareaExisteFinder;
    private final AppLogger logger;

    @Override
    public GeneracionPlanRealResult ejecutar(GeneracionPlanRealDomain entrada) {
        var planEstimado = planEstimadoDeTareaFinder.obtener(entrada.getTarea());
        var planRealExiste = planRealDeTareaExisteFinder.obtener(entrada.getTarea());

        logger.debug(PlanRealKey.LOG_VERIFICACION_GENERAR, !planEstimado.esVacio(), planRealExiste);

        if (planEstimado.esVacio()) {
            return GeneracionPlanRealResultMapper.toResultDescartada(entrada);
        }

        var planReal = PlanRealDomain.crear(
                entrada.getTarea(),
                planEstimado.getCreadoEn(),
                entrada.getFinalizadaEn(),
                planEstimado.getDescripcionEstimado());

        if (planRealExiste) {
            planRealOutputPort.actualizarPorTarea(
                    planReal.getTarea(), planReal.getTiempoRealDias(), planReal.getDescripcionReal());
            return GeneracionPlanRealResultMapper.toResultRecalculada(planReal);
        }

        planRealOutputPort.registrar(PlanRealMapper.toEntity(planReal));
        return GeneracionPlanRealResultMapper.toResultGenerada(planReal);
    }
}
