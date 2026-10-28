package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.primaryadapter.web.dto;

import java.util.List;

public record RegistrarEvaluacionesCuantitativasAsesorRequestDTO(List<EvaluacionDTO> evaluaciones) {

    public record EvaluacionDTO(String item, Integer puntaje) {}
}
