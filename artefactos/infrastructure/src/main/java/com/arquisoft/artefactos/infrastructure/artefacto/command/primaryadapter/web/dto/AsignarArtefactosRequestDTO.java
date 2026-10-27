package com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.dto;

import java.util.List;

public record AsignarArtefactosRequestDTO(
        String proyectoGrado,
        List<String> versionesRepositorioArtefacto,
        List<String> estudiantes) {
}
