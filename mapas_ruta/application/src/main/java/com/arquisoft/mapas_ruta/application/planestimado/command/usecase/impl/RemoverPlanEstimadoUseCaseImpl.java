package com.arquisoft.mapas_ruta.application.planestimado.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.finder.PlanEstimadoExisteFinder;
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
    private final PlanEstimadoExisteFinder planEstimadoExisteFinder;
    private final RemoverPlanEstimadoValidator removerPlanEstimadoValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemocionPlanEstimadoDomain entrada) {
        logger.info(PlanEstimadoKey.LOG_REMOVIENDO, entrada.getPlanEstimado());

        var planExiste = planEstimadoExisteFinder.obtener(entrada.getPlanEstimado());

        logger.debug(PlanEstimadoKey.LOG_VERIFICACION_REMOVER, planExiste);
        removerPlanEstimadoValidator.validar(entrada, planExiste);

        planEstimadoOutputPort.eliminar(entrada.getPlanEstimado());

        logger.info(PlanEstimadoKey.LOG_REMOVIDO, entrada.getPlanEstimado());
    }
}
