package com.arquisoft.mapas_ruta.domain.proyectogrado.rules.impl;

import com.arquisoft.mapas_ruta.domain.proyectogrado.exception.ProyectoGradoNoEnProcesoException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoActualProyectoGrado;
import com.arquisoft.mapas_ruta.domain.proyectogrado.rules.ProyectoGradoEnProcesoRule;

public class ProyectoGradoEnProcesoRuleImpl implements ProyectoGradoEnProcesoRule {

    @Override
    public void validar(EstadoActualProyectoGrado estadoActual) {
        if (!estadoActual.estado().estaEnProceso()) {
            throw new ProyectoGradoNoEnProcesoException(estadoActual.proyectoGrado(), estadoActual.estado().getNombre());
        }
    }
}
