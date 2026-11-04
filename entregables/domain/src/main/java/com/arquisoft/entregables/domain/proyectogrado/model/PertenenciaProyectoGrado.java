package com.arquisoft.entregables.domain.proyectogrado.model;

import java.util.UUID;

public record PertenenciaProyectoGrado(UUID proyectoGrado, UUID estudiante, boolean pertenece) {}
