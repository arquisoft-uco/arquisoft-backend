package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.exception.EstudianteNoParticipaArtefactoException;
import com.arquisoft.artefactos.domain.revisionasesor.model.ParticipacionEstudianteArtefacto;
import com.arquisoft.artefactos.domain.revisionasesor.rules.EstudianteParticipaArtefactoRule;

public class EstudianteParticipaArtefactoRuleImpl implements EstudianteParticipaArtefactoRule {

    @Override
    public void validar(ParticipacionEstudianteArtefacto entrada) {
        if (!entrada.participa()) {
            throw new EstudianteNoParticipaArtefactoException(entrada.estudiante(), entrada.revisionAsesor());
        }
    }
}
