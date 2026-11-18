package com.arquisoft.mapas_ruta.application.planestimado.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.finder.PlanEstimadoPorIdFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.finder.TareaPorIdFinder;
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
    private final PlanEstimadoPorIdFinder planEstimadoPorIdFinder;
    private final TareaPorIdFinder tareaPorIdFinder;
    private final ModificarPlanEstimadoValidator modificarPlanEstimadoValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificacionPlanEstimadoDomain entrada) {
        logger.info(PlanEstimadoKey.LOG_MODIFICANDO, entrada.getPlanEstimado());

        var plan = planEstimadoPorIdFinder.obtener(entrada.getPlanEstimado());
        var tarea = tareaPorIdFinder.obtener(plan.getTarea());
        var planExiste = !plan.esVacio();

        logger.debug(PlanEstimadoKey.LOG_VERIFICACION_MODIFICAR, planExiste);
        modificarPlanEstimadoValidator.validar(entrada, planExiste, tarea);

        planEstimadoOutputPort.actualizarDescripcion(entrada.getPlanEstimado(), entrada.getDescripcionEstimado());

        logger.info(PlanEstimadoKey.LOG_MODIFICADO, entrada.getPlanEstimado());
    }
}
