package com.arquisoft.mapas_ruta.application.planestimado.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.finder.PlanEstimadoExisteFinder;
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
    private final PlanEstimadoExisteFinder planEstimadoExisteFinder;
    private final AportarPlanEstimadoValidator aportarPlanEstimadoValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(AportePlanEstimadoDomain entrada) {
        logger.info(PlanEstimadoKey.LOG_APORTANDO, entrada.getPlanEstimado());

        var planExiste = planEstimadoExisteFinder.obtener(entrada.getPlanEstimado());

        logger.debug(PlanEstimadoKey.LOG_VERIFICACION_APORTAR, planExiste);
        aportarPlanEstimadoValidator.validar(entrada, planExiste);

        planEstimadoOutputPort.actualizarDescripcion(entrada.getPlanEstimado(), entrada.getDescripcionEstimado());

        logger.info(PlanEstimadoKey.LOG_APORTADO, entrada.getPlanEstimado());
    }
}
