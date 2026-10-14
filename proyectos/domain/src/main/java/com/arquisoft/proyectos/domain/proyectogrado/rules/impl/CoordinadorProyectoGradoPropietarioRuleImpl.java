package com.arquisoft.proyectos.domain.proyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoPerteneceCoordinadorException;
import com.arquisoft.proyectos.domain.proyectogrado.model.PropiedadCoordinadorProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.rules.CoordinadorProyectoGradoPropietarioRule;
import com.arquisoft.shared.util.UtilObjeto;

public class CoordinadorProyectoGradoPropietarioRuleImpl implements CoordinadorProyectoGradoPropietarioRule {

    @Override
    public void validar(PropiedadCoordinadorProyectoGrado propiedad) {
        if (UtilObjeto.esNulo(propiedad.coordinadorEsperado())
                || !propiedad.coordinadorEsperado().equals(propiedad.coordinadorSolicitante())) {
            throw new ProyectoGradoNoPerteneceCoordinadorException(
                    propiedad.proyectoGrado(), propiedad.coordinadorSolicitante());
        }
    }
}
