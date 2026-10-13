package com.arquisoft.mapas_ruta.domain.asignacionasesor.rules.impl;

import com.arquisoft.mapas_ruta.domain.asignacionasesor.exception.AsesorNoAsignadoProyectoGradoException;
import com.arquisoft.mapas_ruta.domain.asignacionasesor.model.AsignacionAsesorProyectoGrado;
import com.arquisoft.mapas_ruta.domain.asignacionasesor.rules.AsesorAsignadoProyectoGradoRule;

public class AsesorAsignadoProyectoGradoRuleImpl implements AsesorAsignadoProyectoGradoRule {

    @Override
    public void validar(AsignacionAsesorProyectoGrado asignacion) {
        if (!asignacion.asignado()) {
            throw new AsesorNoAsignadoProyectoGradoException(asignacion.asesor(), asignacion.proyectoGrado());
        }
    }
}
