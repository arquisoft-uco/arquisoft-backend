package com.arquisoft.mapas_ruta.application.planreal.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.planreal.command.finder.PlanRealExisteFinder;
import com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.PlanRealOutputPort;
import com.arquisoft.mapas_ruta.application.planreal.command.usecase.RemoverPlanRealUseCase;
import com.arquisoft.mapas_ruta.application.planreal.command.validator.RemoverPlanRealValidator;
import com.arquisoft.mapas_ruta.domain.planreal.RemocionPlanRealDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.PlanRealKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverPlanRealUseCaseImpl implements RemoverPlanRealUseCase {

    private final PlanRealOutputPort planRealOutputPort;
    private final PlanRealExisteFinder planRealExisteFinder;
    private final RemoverPlanRealValidator removerPlanRealValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemocionPlanRealDomain entrada) {
        logger.info(PlanRealKey.LOG_REMOVIENDO, entrada.getPlanReal());

        var planExiste = planRealExisteFinder.obtener(entrada.getPlanReal());

        logger.debug(PlanRealKey.LOG_VERIFICACION_REMOVER, planExiste);
        removerPlanRealValidator.validar(entrada, planExiste);

        planRealOutputPort.eliminarPorId(entrada.getPlanReal());

        logger.info(PlanRealKey.LOG_REMOVIDO, entrada.getPlanReal());
    }
}
