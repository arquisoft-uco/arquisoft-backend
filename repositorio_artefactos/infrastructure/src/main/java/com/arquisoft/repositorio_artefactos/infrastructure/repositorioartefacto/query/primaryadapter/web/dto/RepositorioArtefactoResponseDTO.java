package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.query.primaryadapter.web.dto;

import com.arquisoft.repositorio_artefactos.infrastructure.administrador.query.primaryadapter.web.dto.AdministradorResponseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RepositorioArtefactoResponseDTO(
        UUID id,
        String nombreRepositorio,
        String descripcion,
        AdministradorResponseDTO administrador
) {
}
