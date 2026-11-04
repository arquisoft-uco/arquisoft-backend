package com.arquisoft.mapas_ruta.application.planestimado.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.finder.PlanEstimadoExisteFinder;
import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.PlanEstimadoOutputPort;
import com.arquisoft.mapas_ruta.application.planestimado.command.usecase.ModificarPlanEstimadoUseCase;
import com.arquisoft.mapas_ruta.application.planestimado.command.validator.ModificarPlanEstimadoValidator;
import com.arquisoft.mapas_ruta.domain.planestimado.ModificacionPlanEstimadoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.PlanEstimadoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarPlanEstimadoUseCaseImpl implements ModificarPlanEstimadoUseCase {

    private final PlanEstimadoOutputPort planEstimadoOutputPort;
    private final PlanEstimadoExisteFinder planEstimadoExisteFinder;
    private final ModificarPlanEstimadoValidator modificarPlanEstimadoValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificacionPlanEstimadoDomain entrada) {
        logger.info(PlanEstimadoKey.LOG_MODIFICANDO, entrada.getPlanEstimado());

        var planExiste = planEstimadoExisteFinder.obtener(entrada.getPlanEstimado());

        logger.debug(PlanEstimadoKey.LOG_VERIFICACION_MODIFICAR, planExiste);
        modificarPlanEstimadoValidator.validar(entrada, planExiste);

        planEstimadoOutputPort.actualizarDescripcion(entrada.getPlanEstimado(), entrada.getDescripcionEstimado());

        logger.info(PlanEstimadoKey.LOG_MODIFICADO, entrada.getPlanEstimado());
    }
}
