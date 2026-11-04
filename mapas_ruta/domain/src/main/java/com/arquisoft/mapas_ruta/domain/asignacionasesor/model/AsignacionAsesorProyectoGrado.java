package com.arquisoft.mapas_ruta.domain.asignacionasesor.model;

import java.util.UUID;

public record AsignacionAsesorProyectoGrado(UUID proyectoGrado, String tituloProyecto, UUID asesor, boolean asignado) {}
