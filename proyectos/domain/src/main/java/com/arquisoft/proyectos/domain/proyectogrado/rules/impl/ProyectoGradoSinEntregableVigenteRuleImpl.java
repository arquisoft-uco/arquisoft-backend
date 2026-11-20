package com.arquisoft.proyectos.domain.proyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoConEntregableVigenteException;
import com.arquisoft.proyectos.domain.proyectogrado.model.EstadoUltimoEntregable;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoSinEntregableVigenteRule;

import java.util.EnumSet;
import java.util.Set;

public class ProyectoGradoSinEntregableVigenteRuleImpl implements ProyectoGradoSinEntregableVigenteRule {

    private static final Set<EstadoEntregable> ESTADOS_BLOQUEANTES = EnumSet.of(
            EstadoEntregable.DISPONIBLE_EVALUACION,
            EstadoEntregable.APROBADO,
            EstadoEntregable.APROBADO_CON_OBSERVACIONES,
            EstadoEntregable.CERRADO);

    @Override
    public void validar(EstadoUltimoEntregable ultimo) {
        if (ESTADOS_BLOQUEANTES.contains(ultimo.estadoEntregable())) {
            throw new ProyectoGradoConEntregableVigenteException(ultimo.proyectoGrado(), ultimo.tituloProyecto(),
                    ultimo.version(), ultimo.estadoEntregable());
        }
    }
}
