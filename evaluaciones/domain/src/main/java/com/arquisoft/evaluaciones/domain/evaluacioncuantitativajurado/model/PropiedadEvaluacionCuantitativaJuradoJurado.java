package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model;

import java.util.UUID;

public record PropiedadEvaluacionCuantitativaJuradoJurado(
        UUID evaluacionCuantitativaJurado, UUID juradoAsignado, UUID juradoSolicitante) {}
