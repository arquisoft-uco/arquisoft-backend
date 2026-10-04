package com.arquisoft.proyectos.domain.estudianteproyectogrado.model;

import java.util.Set;
import java.util.UUID;

public record VigenciaEstudiantesProyectoGrado(Set<UUID> solicitados, Set<UUID> vigentes) {}
