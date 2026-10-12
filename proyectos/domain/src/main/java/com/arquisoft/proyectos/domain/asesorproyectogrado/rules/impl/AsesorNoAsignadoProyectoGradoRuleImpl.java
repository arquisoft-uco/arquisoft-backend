package com.arquisoft.proyectos.domain.asesorproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.AsesorYaAsignadoException;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.AsignacionVigenteAsesor;
import com.arquisoft.proyectos.domain.asesorproyectogrado.rules.AsesorNoAsignadoProyectoGradoRule;

public class AsesorNoAsignadoProyectoGradoRuleImpl implements AsesorNoAsignadoProyectoGradoRule {

    @Override
    public void validar(AsignacionVigenteAsesor asignacion) {
        if (asignacion.yaAsignado()) {
            throw new AsesorYaAsignadoException(asignacion.asesor());
        }
    }
}
