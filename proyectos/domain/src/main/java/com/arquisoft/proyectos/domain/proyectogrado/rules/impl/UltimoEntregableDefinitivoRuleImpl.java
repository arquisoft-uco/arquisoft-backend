package com.arquisoft.proyectos.domain.proyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.proyectos.domain.proyectogrado.exception.UltimoEntregableNoDefinitivoException;
import com.arquisoft.proyectos.domain.proyectogrado.model.EstadoUltimoEntregable;
import com.arquisoft.proyectos.domain.proyectogrado.rules.UltimoEntregableDefinitivoRule;

public class UltimoEntregableDefinitivoRuleImpl implements UltimoEntregableDefinitivoRule {

    @Override
    public void validar(EstadoUltimoEntregable ultimo) {
        var definitivo = ultimo.estadoEntregable() == EstadoEntregable.CERRADO
                || ultimo.estadoEntregable() == EstadoEntregable.RECHAZADO;
        if (!definitivo) {
            throw new UltimoEntregableNoDefinitivoException(ultimo.proyectoGrado(), ultimo.tituloProyecto(),
                    ultimo.version(), ultimo.estadoEntregable());
        }
    }
}
