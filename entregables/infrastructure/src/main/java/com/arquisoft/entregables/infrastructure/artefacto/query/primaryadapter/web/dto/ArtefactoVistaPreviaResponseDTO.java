package com.arquisoft.entregables.infrastructure.artefacto.query.primaryadapter.web.dto;

import java.util.UUID;

public record ArtefactoVistaPreviaResponseDTO(UUID artefacto, String documento, int numeroVersion) {
}
