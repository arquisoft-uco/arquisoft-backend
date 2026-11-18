package com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EntregableVigenteYaExisteException;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.ExistenciaEntregableVigente;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.EntregableVigenteUnicoRule;

public class EntregableVigenteUnicoRuleImpl implements EntregableVigenteUnicoRule {

    @Override
    public void validar(ExistenciaEntregableVigente existencia) {
        if (existencia.existeVigente()) {
            throw new EntregableVigenteYaExisteException(existencia.proyectoGrado(), existencia.nombreProyecto());
        }
    }
}
