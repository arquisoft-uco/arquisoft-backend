package com.arquisoft.proyectos.domain.asesorproyectogrado.model;

import java.util.UUID;

public record AsignacionVigenteAsesor(UUID asesor, String nombreAsesor, UUID proyectoGrado, boolean yaAsignado) {}
