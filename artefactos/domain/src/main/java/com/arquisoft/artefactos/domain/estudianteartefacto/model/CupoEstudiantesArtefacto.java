package com.arquisoft.artefactos.domain.estudianteartefacto.model;

import com.arquisoft.shared.util.UtilColeccion;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public record CupoEstudiantesArtefacto(
        List<UUID> solicitados,
        List<UUID> vinculadosVigentes,
        List<UUID> vigentesProyecto) {

    public CupoEstudiantesArtefacto {
        solicitados = List.copyOf(UtilColeccion.aplicarPorDefecto(solicitados));
        vinculadosVigentes = List.copyOf(UtilColeccion.aplicarPorDefecto(vinculadosVigentes));
        vigentesProyecto = List.copyOf(UtilColeccion.aplicarPorDefecto(vigentesProyecto));
    }

    public List<UUID> nuevos() {
        var conservados = new HashSet<>(vinculadosVigentes);
        return solicitados.stream()
                .filter(estudiante -> !conservados.contains(estudiante))
                .toList();
    }
}
