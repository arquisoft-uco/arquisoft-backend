package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web.dto;

import java.util.List;

public record RegistrarEvaluacionesCuantitativasJuradoRequestDTO(List<EvaluacionDTO> evaluaciones) {

    public record EvaluacionDTO(String item, Integer puntaje) {}
}
