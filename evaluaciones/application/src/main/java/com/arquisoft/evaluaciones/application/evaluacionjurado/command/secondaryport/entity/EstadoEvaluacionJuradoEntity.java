package com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity;

import java.util.UUID;

public record EstadoEvaluacionJuradoEntity(UUID id, UUID jurado, String estado) {
}
