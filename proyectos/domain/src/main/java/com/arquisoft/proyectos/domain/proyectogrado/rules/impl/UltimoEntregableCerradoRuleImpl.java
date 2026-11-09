package com.arquisoft.proyectos.domain.proyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.proyectos.domain.proyectogrado.exception.UltimoEntregableNoCerradoException;
import com.arquisoft.proyectos.domain.proyectogrado.model.EstadoUltimoEntregable;
import com.arquisoft.proyectos.domain.proyectogrado.rules.UltimoEntregableCerradoRule;

public class UltimoEntregableCerradoRuleImpl implements UltimoEntregableCerradoRule {

    @Override
    public void validar(EstadoUltimoEntregable ultimo) {
        if (ultimo.estadoEntregable() != EstadoEntregable.CERRADO) {
            throw new UltimoEntregableNoCerradoException(ultimo.proyectoGrado(), ultimo.tituloProyecto(),
                    ultimo.version(), ultimo.estadoEntregable());
        }
    }
}
