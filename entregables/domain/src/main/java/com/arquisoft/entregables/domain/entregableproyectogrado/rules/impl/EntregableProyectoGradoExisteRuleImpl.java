package com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EntregableProyectoGradoNoEncontradoException;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.ExistenciaEntregableProyectoGrado;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.EntregableProyectoGradoExisteRule;

public class EntregableProyectoGradoExisteRuleImpl implements EntregableProyectoGradoExisteRule {

    @Override
    public void validar(ExistenciaEntregableProyectoGrado existencia) {
        if (!existencia.existe()) {
            throw new EntregableProyectoGradoNoEncontradoException(existencia.entregableProyectoGrado());
        }
    }
}
