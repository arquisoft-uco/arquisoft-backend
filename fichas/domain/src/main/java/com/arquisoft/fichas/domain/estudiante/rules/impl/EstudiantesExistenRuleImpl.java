package com.arquisoft.fichas.domain.estudiante.rules.impl;

import com.arquisoft.fichas.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.domain.estudiante.secondaryport.EstudianteOutputPort;
import com.arquisoft.fichas.domain.estudiante.rules.EstudiantesExistenRule;
import com.arquisoft.shared.util.UtilCollection;

import java.util.List;
import java.util.UUID;

public class EstudiantesExistenRuleImpl implements EstudiantesExistenRule {

    private final EstudianteOutputPort estudianteOutputPort;

    public EstudiantesExistenRuleImpl(EstudianteOutputPort estudianteOutputPort) {
        this.estudianteOutputPort = estudianteOutputPort;
    }

    @Override
    public void validar(List<UUID> estudiantes) {
        if (UtilCollection.isEmptyOrNull(estudiantes)) {
            return;
        }
        estudiantes.forEach(estudiante -> {
            if (!estudianteOutputPort.existePorId(estudiante)) {
                throw new EstudianteNoEncontradoException(estudiante);
            }
        });
    }
}
