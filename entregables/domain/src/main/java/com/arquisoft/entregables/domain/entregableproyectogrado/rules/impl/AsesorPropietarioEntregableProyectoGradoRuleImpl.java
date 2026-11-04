package com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.exception.AsesorNoPropietarioEntregableProyectoGradoException;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.PropiedadEntregableProyectoGrado;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.AsesorPropietarioEntregableProyectoGradoRule;

public class AsesorPropietarioEntregableProyectoGradoRuleImpl implements AsesorPropietarioEntregableProyectoGradoRule {

    @Override
    public void validar(PropiedadEntregableProyectoGrado propiedad) {
        if (!propiedad.asesorAsignado().equals(propiedad.asesorSolicitante())) {
            throw new AsesorNoPropietarioEntregableProyectoGradoException(
                    propiedad.asesorSolicitante(), propiedad.entregableProyectoGrado());
        }
    }
}
