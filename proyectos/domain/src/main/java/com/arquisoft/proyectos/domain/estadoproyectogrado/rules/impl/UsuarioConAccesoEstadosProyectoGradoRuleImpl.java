package com.arquisoft.proyectos.domain.estadoproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estadoproyectogrado.exception.UsuarioSinAccesoEstadosProyectoGradoException;
import com.arquisoft.proyectos.domain.estadoproyectogrado.model.AccesoEstadosProyectoGrado;
import com.arquisoft.proyectos.domain.estadoproyectogrado.rules.UsuarioConAccesoEstadosProyectoGradoRule;
import com.arquisoft.shared.util.UtilObjeto;

public class UsuarioConAccesoEstadosProyectoGradoRuleImpl implements UsuarioConAccesoEstadosProyectoGradoRule {

    @Override
    public void validar(AccesoEstadosProyectoGrado acceso) {
        var esCoordinador = UtilObjeto.noEsNulo(acceso.usuario())
                && acceso.usuario().equals(acceso.coordinadorDelProyecto());
        if (!esCoordinador && !acceso.asesorAsignado()) {
            throw new UsuarioSinAccesoEstadosProyectoGradoException(acceso.proyectoGrado(), acceso.usuario());
        }
    }
}
