package com.arquisoft.proyectos.domain.asesorproyectogrado.model;

import java.util.UUID;

public record ExistenciaAsesorProyectoGrado(UUID asesor, UUID proyectoGrado, boolean existe) {}
