package com.arquisoft.proyectos.domain.asesorproyectogrado.model;

import java.util.UUID;

public record AsignacionVigenteAsesor(UUID asesor, UUID proyectoGrado, boolean yaAsignado) {}
