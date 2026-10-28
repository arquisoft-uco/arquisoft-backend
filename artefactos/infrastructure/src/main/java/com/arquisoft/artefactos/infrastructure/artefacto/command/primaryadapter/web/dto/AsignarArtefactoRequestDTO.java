package com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.dto;

import java.util.List;

public record AsignarArtefactoRequestDTO(
        String proyectoGrado,
        String versionRepositorioArtefacto,
        List<String> estudiantes) {
}
