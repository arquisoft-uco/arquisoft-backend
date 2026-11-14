package com.arquisoft.entregables.infrastructure.artefacto.query.primaryadapter.web.dto;

import java.util.List;

public record VistaPreviaPaqueteResponseDTO(int cantidad, List<ArtefactoVistaPreviaResponseDTO> artefactos) {
}
