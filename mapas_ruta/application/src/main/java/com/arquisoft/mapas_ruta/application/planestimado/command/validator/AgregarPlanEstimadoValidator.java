package com.arquisoft.mapas_ruta.application.planestimado.command.validator;

import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.planestimado.PlanEstimadoDomain;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;

public interface AgregarPlanEstimadoValidator {

    void validar(PlanEstimadoDomain planEstimado, TareaDomain tarea, MapaRutaDomain mapaRuta,
                 boolean planExiste, long diasAcumulados);
}
