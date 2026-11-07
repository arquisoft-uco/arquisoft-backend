package com.arquisoft.mapas_ruta.application.planreal.command.result.mapper;

import com.arquisoft.mapas_ruta.application.planreal.command.result.GeneracionPlanRealResult;
import com.arquisoft.mapas_ruta.domain.planreal.GeneracionPlanRealDomain;
import com.arquisoft.mapas_ruta.domain.planreal.PlanRealDomain;

public final class GeneracionPlanRealResultMapper {

    private GeneracionPlanRealResultMapper() {}

    public static GeneracionPlanRealResult.Generada toResultGenerada(PlanRealDomain planReal) {
        return new GeneracionPlanRealResult.Generada(planReal.getTarea(), planReal.getTiempoRealDias());
    }

    public static GeneracionPlanRealResult.Recalculada toResultRecalculada(PlanRealDomain planReal) {
        return new GeneracionPlanRealResult.Recalculada(planReal.getTarea(), planReal.getTiempoRealDias());
    }

    public static GeneracionPlanRealResult.Descartada toResultDescartada(GeneracionPlanRealDomain generacion) {
        return new GeneracionPlanRealResult.Descartada(generacion.getTarea());
    }
}
