package com.arquisoft.mapas_ruta.domain.tarea.rules.impl;

import com.arquisoft.mapas_ruta.domain.tarea.exception.TransicionEstadoItemNoPermitidaException;
import com.arquisoft.mapas_ruta.domain.tarea.model.TransicionEstadoItem;
import com.arquisoft.mapas_ruta.domain.tarea.rules.TransicionEstadoItemPermitidaRule;

public class TransicionEstadoItemPermitidaRuleImpl implements TransicionEstadoItemPermitidaRule {

    @Override
    public void validar(TransicionEstadoItem transicion) {
        if (!transicion.actual().puedePasarA(transicion.nuevo())) {
            throw new TransicionEstadoItemNoPermitidaException(
                    transicion.tarea(), transicion.actual(), transicion.nuevo());
        }
    }
}
