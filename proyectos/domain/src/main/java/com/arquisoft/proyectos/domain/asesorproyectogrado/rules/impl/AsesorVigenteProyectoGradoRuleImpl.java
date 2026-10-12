package com.arquisoft.proyectos.domain.asesorproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.AsesorNoVigenteException;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.VigenciaAsesorProyectoGrado;
import com.arquisoft.proyectos.domain.asesorproyectogrado.rules.AsesorVigenteProyectoGradoRule;

public class AsesorVigenteProyectoGradoRuleImpl implements AsesorVigenteProyectoGradoRule {

    @Override
    public void validar(VigenciaAsesorProyectoGrado vigencia) {
        if (!vigencia.existe() || vigencia.eliminado()) {
            throw new AsesorNoVigenteException(vigencia.asesor());
        }
    }
}
