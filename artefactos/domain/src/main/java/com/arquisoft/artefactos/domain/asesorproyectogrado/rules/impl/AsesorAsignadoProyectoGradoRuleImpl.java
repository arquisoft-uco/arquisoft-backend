package com.arquisoft.artefactos.domain.asesorproyectogrado.rules.impl;

import com.arquisoft.artefactos.domain.asesorproyectogrado.exception.AsesorNoAsignadoProyectoGradoException;
import com.arquisoft.artefactos.domain.asesorproyectogrado.model.VigenciaAsesorProyectoGrado;
import com.arquisoft.artefactos.domain.asesorproyectogrado.rules.AsesorAsignadoProyectoGradoRule;

public class AsesorAsignadoProyectoGradoRuleImpl implements AsesorAsignadoProyectoGradoRule {

    @Override
    public void validar(VigenciaAsesorProyectoGrado vigencia) {
        var encontrado = vigencia.encontrado();
        if (encontrado.esVacio() || encontrado.estaFinalizado()) {
            throw new AsesorNoAsignadoProyectoGradoException(vigencia.asesor(), vigencia.proyectoGrado());
        }
    }
}
