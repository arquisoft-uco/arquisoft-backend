package com.arquisoft.artefactos.domain.proyectogrado.model;

import java.util.UUID;

public record EstadoProyectoGrado(UUID proyectoGrado, String nombre, boolean congelado, boolean concluido) {}
