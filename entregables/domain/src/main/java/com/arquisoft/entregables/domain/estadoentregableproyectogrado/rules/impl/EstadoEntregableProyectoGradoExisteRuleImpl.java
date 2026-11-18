package com.arquisoft.entregables.domain.estadoentregableproyectogrado.rules.impl;

import com.arquisoft.entregables.domain.estadoentregableproyectogrado.exception.EstadoEntregableProyectoGradoNoEncontradoException;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.model.ExistenciaEstadoEntregableProyectoGrado;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.rules.EstadoEntregableProyectoGradoExisteRule;

public class EstadoEntregableProyectoGradoExisteRuleImpl implements EstadoEntregableProyectoGradoExisteRule {

    @Override
    public void validar(ExistenciaEstadoEntregableProyectoGrado existencia) {
        if (!existencia.existe()) {
            throw new EstadoEntregableProyectoGradoNoEncontradoException(existencia.entregableProyectoGrado());
        }
    }
}
