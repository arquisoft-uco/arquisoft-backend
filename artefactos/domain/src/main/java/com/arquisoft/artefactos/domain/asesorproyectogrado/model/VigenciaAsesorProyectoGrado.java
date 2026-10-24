package com.arquisoft.artefactos.domain.asesorproyectogrado.model;

import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;

import java.util.UUID;

public record VigenciaAsesorProyectoGrado(UUID asesor, UUID proyectoGrado, AsesorProyectoGradoDomain encontrado) {}
