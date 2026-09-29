package com.arquisoft.mapas_ruta.domain.proyectogrado.rules.impl;

import com.arquisoft.mapas_ruta.domain.proyectogrado.exception.ProyectoGradoNoPropietarioException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.PropiedadProyectoGrado;
import com.arquisoft.mapas_ruta.domain.proyectogrado.rules.CoordinadorPropietarioProyectoGradoRule;

public class CoordinadorPropietarioProyectoGradoRuleImpl implements CoordinadorPropietarioProyectoGradoRule {

    @Override
    public void validar(PropiedadProyectoGrado propiedad) {
        if (!propiedad.coordinadorAsignado().equals(propiedad.coordinadorSolicitante())) {
            throw new ProyectoGradoNoPropietarioException(propiedad.proyectoGrado());
        }
    }
}
