package com.arquisoft.mapas_ruta.application.planreal.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.planreal.command.finder.PlanRealExisteFinder;
import com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.PlanRealOutputPort;
import com.arquisoft.mapas_ruta.application.planreal.command.usecase.AportarPlanRealUseCase;
import com.arquisoft.mapas_ruta.application.planreal.command.validator.AportarPlanRealValidator;
import com.arquisoft.mapas_ruta.domain.planreal.AportePlanRealDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.PlanRealKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AportarPlanRealUseCaseImpl implements AportarPlanRealUseCase {

    private final PlanRealOutputPort planRealOutputPort;
    private final PlanRealExisteFinder planRealExisteFinder;
    private final AportarPlanRealValidator aportarPlanRealValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(AportePlanRealDomain entrada) {
        logger.info(PlanRealKey.LOG_APORTANDO, entrada.getPlanReal());

        var planExiste = planRealExisteFinder.obtener(entrada.getPlanReal());

        logger.debug(PlanRealKey.LOG_VERIFICACION_APORTAR, planExiste);
        aportarPlanRealValidator.validar(entrada, planExiste);

        planRealOutputPort.actualizarDescripcionPorId(entrada.getPlanReal(), entrada.getDescripcionReal());

        logger.info(PlanRealKey.LOG_APORTADO, entrada.getPlanReal());
    }
}
