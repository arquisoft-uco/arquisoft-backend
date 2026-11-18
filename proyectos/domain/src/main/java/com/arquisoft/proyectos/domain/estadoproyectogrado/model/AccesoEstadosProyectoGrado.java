package com.arquisoft.proyectos.domain.estadoproyectogrado.model;

import java.util.UUID;

public record AccesoEstadosProyectoGrado(
        UUID proyectoGrado,
        UUID usuario,
        UUID coordinadorDelProyecto,
        boolean asesorAsignado) {}
