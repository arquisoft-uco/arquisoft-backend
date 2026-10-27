package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.RepositorioArtefactoYaAsignadoException;
import com.arquisoft.artefactos.domain.artefacto.model.DisponibilidadRepositoriosProyecto;
import com.arquisoft.artefactos.domain.artefacto.rules.RepositorioArtefactoUnicoEnProyectoRule;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.UUID;

public class RepositorioArtefactoUnicoEnProyectoRuleImpl implements RepositorioArtefactoUnicoEnProyectoRule {

    @Override
    public void validar(DisponibilidadRepositoriosProyecto disponibilidad) {
        var vistos = new HashSet<UUID>(disponibilidad.repositoriosAsignados());
        var conflictos = new LinkedHashSet<UUID>();
        for (var version : disponibilidad.versiones()) {
            if (!vistos.add(version.getRepositorioArtefacto())) {
                conflictos.add(version.getRepositorioArtefacto());
            }
        }
        if (!conflictos.isEmpty()) {
            throw new RepositorioArtefactoYaAsignadoException(conflictos.stream().toList());
        }
    }
}
