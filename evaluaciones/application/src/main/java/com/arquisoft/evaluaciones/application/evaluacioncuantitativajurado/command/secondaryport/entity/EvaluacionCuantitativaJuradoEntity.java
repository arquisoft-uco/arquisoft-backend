package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.entity;

import java.util.UUID;

public record EvaluacionCuantitativaJuradoEntity(
        UUID id, UUID evaluacionJurado, UUID item, Integer puntaje) {
}
