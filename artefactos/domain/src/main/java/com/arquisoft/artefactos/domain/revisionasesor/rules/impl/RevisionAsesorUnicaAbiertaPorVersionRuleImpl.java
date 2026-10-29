package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorAbiertaExistenteException;
import com.arquisoft.artefactos.domain.revisionasesor.model.UnicidadRevisionAbiertaVersion;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorUnicaAbiertaPorVersionRule;

public class RevisionAsesorUnicaAbiertaPorVersionRuleImpl implements RevisionAsesorUnicaAbiertaPorVersionRule {

    @Override
    public void validar(UnicidadRevisionAbiertaVersion entrada) {
        if (entrada.existeAbierta()) {
            throw new RevisionAsesorAbiertaExistenteException(entrada.versionArtefacto(), entrada.numeroVersion());
        }
    }
}
