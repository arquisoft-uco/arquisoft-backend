package com.arquisoft.mapas_ruta.domain.tarea.rules.impl;

import com.arquisoft.mapas_ruta.domain.tarea.exception.TareaNoEncontradaException;
import com.arquisoft.mapas_ruta.domain.tarea.model.ExistenciaTarea;
import com.arquisoft.mapas_ruta.domain.tarea.rules.TareaExisteRule;

public class TareaExisteRuleImpl implements TareaExisteRule {

    @Override
    public void validar(ExistenciaTarea existencia) {
        if (!existencia.existe()) {
            throw new TareaNoEncontradaException(existencia.tarea());
        }
    }
}
