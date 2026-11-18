package com.arquisoft.entregables.domain.entregableproyectogrado.model;

import java.util.UUID;

public record ExistenciaEntregableVigente(UUID proyectoGrado, String nombreProyecto, boolean existeVigente) {}
