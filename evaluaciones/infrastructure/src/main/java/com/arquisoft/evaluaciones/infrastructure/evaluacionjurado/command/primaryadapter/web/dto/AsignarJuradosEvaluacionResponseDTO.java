package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.primaryadapter.web.dto;

import java.util.List;
import java.util.UUID;

public record AsignarJuradosEvaluacionResponseDTO(List<UUID> evaluacionesJurado) {}
