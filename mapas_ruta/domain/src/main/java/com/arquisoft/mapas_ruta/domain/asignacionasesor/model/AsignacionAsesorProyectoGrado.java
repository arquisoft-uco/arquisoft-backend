package com.arquisoft.mapas_ruta.domain.asignacionasesor.model;

import java.util.UUID;

public record AsignacionAsesorProyectoGrado(UUID proyectoGrado, UUID asesor, boolean asignado) {}
