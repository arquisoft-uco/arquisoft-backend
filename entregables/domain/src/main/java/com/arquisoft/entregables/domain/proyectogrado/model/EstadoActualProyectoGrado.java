package com.arquisoft.entregables.domain.proyectogrado.model;

import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;

import java.util.UUID;

public record EstadoActualProyectoGrado(UUID proyectoGrado, String nombre, EstadoProyecto estadoActual) {}
