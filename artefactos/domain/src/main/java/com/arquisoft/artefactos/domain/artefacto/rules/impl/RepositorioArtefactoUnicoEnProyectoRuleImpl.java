package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.RepositorioArtefactoYaAsignadoException;
import com.arquisoft.artefactos.domain.artefacto.model.DisponibilidadRepositoriosProyecto;
import com.arquisoft.artefactos.domain.artefacto.rules.RepositorioArtefactoUnicoEnProyectoRule;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.UUID;

public class RepositorioArtefactoUnicoEnProyectoRuleImpl implements RepositorioArtefactoUnicoEnProyectoRule {

    @Override
    public void validar(DisponibilidadRepositoriosProyecto disponibilidad) {
        var vistos = new HashSet<UUID>(disponibilidad.repositoriosAsignados());
        var conflictos = new LinkedHashMap<UUID, String>();
        for (var version : disponibilidad.versiones()) {
            if (!vistos.add(version.getRepositorioArtefacto())) {
                conflictos.putIfAbsent(version.getRepositorioArtefacto(), version.getNombreRepositorio());
            }
        }
        if (!conflictos.isEmpty()) {
            throw new RepositorioArtefactoYaAsignadoException(
                    conflictos.keySet().stream().toList(), conflictos.values().stream().toList());
        }
    }
}
