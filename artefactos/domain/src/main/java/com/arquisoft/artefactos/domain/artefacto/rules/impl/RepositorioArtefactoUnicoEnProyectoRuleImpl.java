package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.RepositorioArtefactoYaAsignadoException;
import com.arquisoft.artefactos.domain.artefacto.model.DisponibilidadRepositorioProyecto;
import com.arquisoft.artefactos.domain.artefacto.rules.RepositorioArtefactoUnicoEnProyectoRule;

public class RepositorioArtefactoUnicoEnProyectoRuleImpl implements RepositorioArtefactoUnicoEnProyectoRule {

    @Override
    public void validar(DisponibilidadRepositorioProyecto disponibilidad) {
        var version = disponibilidad.version();
        if (disponibilidad.repositoriosAsignados().contains(version.getRepositorioArtefacto())) {
            throw new RepositorioArtefactoYaAsignadoException(
                    version.getRepositorioArtefacto(), version.getNombreRepositorio());
        }
    }
}
