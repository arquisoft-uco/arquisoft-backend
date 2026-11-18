package com.arquisoft.mapas_ruta.application.planreal.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.planreal.command.finder.PlanRealExisteFinder;
import com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.PlanRealOutputPort;
import com.arquisoft.mapas_ruta.application.planreal.command.usecase.ModificarPlanRealUseCase;
import com.arquisoft.mapas_ruta.application.planreal.command.validator.ModificarPlanRealValidator;
import com.arquisoft.mapas_ruta.domain.planreal.ModificacionPlanRealDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.PlanRealKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarPlanRealUseCaseImpl implements ModificarPlanRealUseCase {

    private final PlanRealOutputPort planRealOutputPort;
    private final PlanRealExisteFinder planRealExisteFinder;
    private final ModificarPlanRealValidator modificarPlanRealValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificacionPlanRealDomain entrada) {
        logger.info(PlanRealKey.LOG_MODIFICANDO, entrada.getPlanReal(), entrada.getTiempoRealDias());

        var planExiste = planRealExisteFinder.obtener(entrada.getPlanReal());

        logger.debug(PlanRealKey.LOG_VERIFICACION_MODIFICAR, planExiste);
        modificarPlanRealValidator.validar(entrada, planExiste);

        planRealOutputPort.actualizarPorId(
                entrada.getPlanReal(), entrada.getTiempoRealDias(), entrada.getDescripcionReal());

        logger.info(PlanRealKey.LOG_MODIFICADO, entrada.getPlanReal());
    }
}
