package com.arquisoft.entregables.domain.asesorproyectogrado.rules.impl;

import com.arquisoft.entregables.domain.asesorproyectogrado.exception.AsesorNoAsignadoProyectoGradoException;
import com.arquisoft.entregables.domain.asesorproyectogrado.model.VigenciaAsesorProyectoGrado;
import com.arquisoft.entregables.domain.asesorproyectogrado.rules.AsesorAsignadoProyectoGradoRule;
import com.arquisoft.shared.util.UtilUUID;

public class AsesorAsignadoProyectoGradoRuleImpl implements AsesorAsignadoProyectoGradoRule {

    @Override
    public void validar(VigenciaAsesorProyectoGrado vigencia) {
        if (UtilUUID.esPorDefecto(vigencia.asesorVigente())
                || !vigencia.asesorVigente().equals(vigencia.asesorSolicitante())) {
            throw new AsesorNoAsignadoProyectoGradoException(vigencia.asesorSolicitante(), vigencia.proyectoGrado());
        }
    }
}
