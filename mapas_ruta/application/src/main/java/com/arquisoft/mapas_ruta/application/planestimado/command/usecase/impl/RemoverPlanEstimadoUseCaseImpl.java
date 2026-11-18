package com.arquisoft.mapas_ruta.application.planestimado.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.finder.PlanEstimadoPorIdFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.finder.TareaPorIdFinder;
import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.PlanEstimadoOutputPort;
import com.arquisoft.mapas_ruta.application.planestimado.command.usecase.RemoverPlanEstimadoUseCase;
import com.arquisoft.mapas_ruta.application.planestimado.command.validator.RemoverPlanEstimadoValidator;
import com.arquisoft.mapas_ruta.domain.planestimado.RemocionPlanEstimadoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.PlanEstimadoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverPlanEstimadoUseCaseImpl implements RemoverPlanEstimadoUseCase {

    private final PlanEstimadoOutputPort planEstimadoOutputPort;
    private final PlanEstimadoPorIdFinder planEstimadoPorIdFinder;
    private final TareaPorIdFinder tareaPorIdFinder;
    private final RemoverPlanEstimadoValidator removerPlanEstimadoValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemocionPlanEstimadoDomain entrada) {
        logger.info(PlanEstimadoKey.LOG_REMOVIENDO, entrada.getPlanEstimado());

        var plan = planEstimadoPorIdFinder.obtener(entrada.getPlanEstimado());
        var tarea = tareaPorIdFinder.obtener(plan.getTarea());
        var planExiste = !plan.esVacio();

        logger.debug(PlanEstimadoKey.LOG_VERIFICACION_REMOVER, planExiste);
        removerPlanEstimadoValidator.validar(entrada, planExiste, tarea);

        planEstimadoOutputPort.eliminar(entrada.getPlanEstimado());

        logger.info(PlanEstimadoKey.LOG_REMOVIDO, entrada.getPlanEstimado());
    }
}
