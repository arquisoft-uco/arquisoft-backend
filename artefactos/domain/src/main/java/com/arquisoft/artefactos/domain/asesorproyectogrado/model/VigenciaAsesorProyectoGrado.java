package com.arquisoft.artefactos.domain.asesorproyectogrado.model;

import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;

import java.util.List;
import java.util.UUID;

public record VigenciaAsesorProyectoGrado(UUID asesor, UUID proyectoGrado, List<AsesorProyectoGradoDomain> masRecientes) {}
