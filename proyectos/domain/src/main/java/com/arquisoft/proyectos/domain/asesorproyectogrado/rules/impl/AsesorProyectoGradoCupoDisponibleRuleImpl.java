package com.arquisoft.proyectos.domain.asesorproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.CupoAsesoresExcedidoException;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.CupoAsesoresProyectoGrado;
import com.arquisoft.proyectos.domain.asesorproyectogrado.rules.AsesorProyectoGradoCupoDisponibleRule;
import com.arquisoft.shared.message.constant.ProyectosLimits;

public class AsesorProyectoGradoCupoDisponibleRuleImpl implements AsesorProyectoGradoCupoDisponibleRule {

    @Override
    public void validar(CupoAsesoresProyectoGrado cupo) {
        if (cupo.asignadosActuales() >= ProyectosLimits.AsesorProyectoGrado.MAX_ASESORES_ASIGNADOS) {
            throw new CupoAsesoresExcedidoException(ProyectosLimits.AsesorProyectoGrado.MAX_ASESORES_ASIGNADOS);
        }
    }
}
