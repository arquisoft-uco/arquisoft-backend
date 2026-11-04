package com.arquisoft.mapas_ruta.domain.tarea.rules.impl;

import com.arquisoft.mapas_ruta.domain.tarea.exception.TareaNoRemovibleException;
import com.arquisoft.mapas_ruta.domain.tarea.model.RemocionTarea;
import com.arquisoft.mapas_ruta.domain.tarea.rules.TareaRemovibleRule;

public class TareaRemovibleRuleImpl implements TareaRemovibleRule {

    @Override
    public void validar(RemocionTarea remocion) {
        if (!remocion.estadoItem().permiteRemocion()) {
            throw new TareaNoRemovibleException(remocion.tarea(), remocion.estadoItem());
        }
    }
}
