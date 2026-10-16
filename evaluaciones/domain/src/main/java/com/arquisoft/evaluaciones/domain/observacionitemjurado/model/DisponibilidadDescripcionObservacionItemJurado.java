package com.arquisoft.evaluaciones.domain.observacionitemjurado.model;

import java.util.UUID;

public record DisponibilidadDescripcionObservacionItemJurado(
        UUID evaluacionCuantitativaJurado, String descripcion, boolean yaExiste) {}
