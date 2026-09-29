package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.primaryadapter.web.dto;

import java.util.List;

public record RegistrarEvaluacionesCualitativasJuradoRequestDTO(List<ParDTO> evaluaciones) {

    public record ParDTO(String item, String criterio) {}
}
