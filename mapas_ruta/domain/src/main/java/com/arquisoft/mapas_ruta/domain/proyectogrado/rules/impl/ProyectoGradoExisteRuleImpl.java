package com.arquisoft.mapas_ruta.domain.proyectogrado.rules.impl;

import com.arquisoft.mapas_ruta.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.mapas_ruta.domain.proyectogrado.rules.ProyectoGradoExisteRule;

public class ProyectoGradoExisteRuleImpl implements ProyectoGradoExisteRule {

    @Override
    public void validar(ExistenciaProyectoGrado existencia) {
        if (!existencia.proyectoGradoExiste()) {
            throw new ProyectoGradoNoEncontradoException(existencia.proyectoGrado());
        }
    }
}
