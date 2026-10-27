package com.arquisoft.proyectos.domain.proyectogrado.model;

import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;

import java.util.UUID;

public record EstadoActualProyectoGrado(UUID proyectoGrado, String tituloProyecto, EstadoProyecto estadoActual) {}
