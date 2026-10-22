package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web.dto;

import java.util.List;

public record RegistrarEvaluacionesCualitativasAsesorRequestDTO(List<ParDTO> evaluaciones) {

    public record ParDTO(String item, String criterio) {}
}
