package com.arquisoft.proyectos.domain.proyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.proyectos.domain.proyectogrado.exception.UltimoEntregableNoTerminalException;
import com.arquisoft.proyectos.domain.proyectogrado.model.EstadoUltimoEntregable;
import com.arquisoft.proyectos.domain.proyectogrado.rules.UltimoEntregableTerminalRule;

public class UltimoEntregableTerminalRuleImpl implements UltimoEntregableTerminalRule {

    @Override
    public void validar(EstadoUltimoEntregable ultimo) {
        if (ultimo.estadoEntregable() != EstadoEntregable.CERRADO
                && ultimo.estadoEntregable() != EstadoEntregable.CANCELADO) {
            throw new UltimoEntregableNoTerminalException(ultimo.proyectoGrado(), ultimo.tituloProyecto(),
                    ultimo.version(), ultimo.estadoEntregable());
        }
    }
}
