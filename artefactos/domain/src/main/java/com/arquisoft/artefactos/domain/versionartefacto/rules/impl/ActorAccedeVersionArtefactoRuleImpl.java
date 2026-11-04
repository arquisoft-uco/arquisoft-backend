package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.ActorSinAccesoVersionArtefactoException;
import com.arquisoft.artefactos.domain.versionartefacto.model.AccesoVersionArtefacto;
import com.arquisoft.artefactos.domain.versionartefacto.rules.ActorAccedeVersionArtefactoRule;

public class ActorAccedeVersionArtefactoRuleImpl implements ActorAccedeVersionArtefactoRule {

    @Override
    public void validar(AccesoVersionArtefacto entrada) {
        var accedeComoAsesor = entrada.comoAsesor() && entrada.actor().equals(entrada.asesor());
        var accedeComoEstudiante = entrada.comoEstudiante() && entrada.participa();

        if (!accedeComoAsesor && !accedeComoEstudiante) {
            throw new ActorSinAccesoVersionArtefactoException(entrada.actor(), entrada.versionArtefacto());
        }
    }
}
