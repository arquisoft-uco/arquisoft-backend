package com.arquisoft.artefactos.domain.asesorproyectogrado.rules.impl;

import com.arquisoft.artefactos.domain.asesorproyectogrado.exception.AsesorNoAsignadoProyectoGradoException;
import com.arquisoft.artefactos.domain.asesorproyectogrado.model.VigenciaAsesorProyectoGrado;
import com.arquisoft.artefactos.domain.asesorproyectogrado.rules.AsesorAsignadoProyectoGradoRule;

public class AsesorAsignadoProyectoGradoRuleImpl implements AsesorAsignadoProyectoGradoRule {

    @Override
    public void validar(VigenciaAsesorProyectoGrado vigencia) {
        var masRecientes = vigencia.masRecientes();
        if (masRecientes.size() != 1) {
            throw noAsignado(vigencia);
        }
        var masReciente = masRecientes.get(0);
        if (!masReciente.getAsesor().equals(vigencia.asesor()) || masReciente.estaFinalizado()) {
            throw noAsignado(vigencia);
        }
    }

    private static AsesorNoAsignadoProyectoGradoException noAsignado(VigenciaAsesorProyectoGrado vigencia) {
        return new AsesorNoAsignadoProyectoGradoException(
                vigencia.asesor(), vigencia.nombreAsesor(), vigencia.proyectoGrado());
    }
}
