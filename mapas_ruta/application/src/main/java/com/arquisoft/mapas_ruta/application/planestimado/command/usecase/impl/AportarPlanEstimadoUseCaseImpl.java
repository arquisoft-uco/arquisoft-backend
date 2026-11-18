package com.arquisoft.mapas_ruta.application.planestimado.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.finder.PlanEstimadoPorIdFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.finder.TareaPorIdFinder;
import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.PlanEstimadoOutputPort;
import com.arquisoft.mapas_ruta.application.planestimado.command.usecase.AportarPlanEstimadoUseCase;
import com.arquisoft.mapas_ruta.application.planestimado.command.validator.AportarPlanEstimadoValidator;
import com.arquisoft.mapas_ruta.domain.planestimado.AportePlanEstimadoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.PlanEstimadoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AportarPlanEstimadoUseCaseImpl implements AportarPlanEstimadoUseCase {

    private final PlanEstimadoOutputPort planEstimadoOutputPort;
    private final PlanEstimadoPorIdFinder planEstimadoPorIdFinder;
    private final TareaPorIdFinder tareaPorIdFinder;
    private final AportarPlanEstimadoValidator aportarPlanEstimadoValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(AportePlanEstimadoDomain entrada) {
        logger.info(PlanEstimadoKey.LOG_APORTANDO, entrada.getPlanEstimado());

        var plan = planEstimadoPorIdFinder.obtener(entrada.getPlanEstimado());
        var tarea = tareaPorIdFinder.obtener(plan.getTarea());
        var planExiste = !plan.esVacio();

        logger.debug(PlanEstimadoKey.LOG_VERIFICACION_APORTAR, planExiste);
        aportarPlanEstimadoValidator.validar(entrada, planExiste, tarea);

        planEstimadoOutputPort.actualizarDescripcion(entrada.getPlanEstimado(), entrada.getDescripcionEstimado());

        logger.info(PlanEstimadoKey.LOG_APORTADO, entrada.getPlanEstimado());
    }
}
