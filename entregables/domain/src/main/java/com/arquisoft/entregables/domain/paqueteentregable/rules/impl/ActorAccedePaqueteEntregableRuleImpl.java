package com.arquisoft.entregables.domain.paqueteentregable.rules.impl;

import com.arquisoft.entregables.domain.paqueteentregable.exception.ActorSinAccesoPaqueteEntregableException;
import com.arquisoft.entregables.domain.paqueteentregable.model.AccesoPaqueteEntregable;
import com.arquisoft.entregables.domain.paqueteentregable.rules.ActorAccedePaqueteEntregableRule;

public class ActorAccedePaqueteEntregableRuleImpl implements ActorAccedePaqueteEntregableRule {

    @Override
    public void validar(AccesoPaqueteEntregable entrada) {
        var accedeComoAsesor = entrada.comoAsesor() && entrada.actor().equals(entrada.asesorVigente());
        var accedeComoEstudiante = entrada.comoEstudiante() && entrada.participa();

        if (!accedeComoAsesor && !accedeComoEstudiante) {
            throw new ActorSinAccesoPaqueteEntregableException(entrada.actor(), entrada.entregableProyectoGrado());
        }
    }
}
