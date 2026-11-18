package com.arquisoft.proyectos.domain.proyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoSinEntregableException;
import com.arquisoft.proyectos.domain.proyectogrado.model.ExistenciaUltimoEntregable;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoConEntregableRule;

public class ProyectoGradoConEntregableRuleImpl implements ProyectoGradoConEntregableRule {

    @Override
    public void validar(ExistenciaUltimoEntregable existencia) {
        if (!existencia.existe()) {
            throw new ProyectoGradoSinEntregableException(existencia.proyectoGrado(), existencia.tituloProyecto());
        }
    }
}
