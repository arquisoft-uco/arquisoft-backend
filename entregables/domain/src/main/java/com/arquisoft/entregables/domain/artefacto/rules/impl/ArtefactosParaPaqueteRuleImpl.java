package com.arquisoft.entregables.domain.artefacto.rules.impl;

import com.arquisoft.entregables.domain.artefacto.exception.ProyectoSinArtefactosParaPaqueteException;
import com.arquisoft.entregables.domain.artefacto.model.ArtefactosDelProyecto;
import com.arquisoft.entregables.domain.artefacto.rules.ArtefactosParaPaqueteRule;

public class ArtefactosParaPaqueteRuleImpl implements ArtefactosParaPaqueteRule {

    @Override
    public void validar(ArtefactosDelProyecto entrada) {
        if (entrada.cantidadVigentes() == 0) {
            throw new ProyectoSinArtefactosParaPaqueteException(entrada.proyectoGrado(), entrada.nombreProyecto());
        }
    }
}
