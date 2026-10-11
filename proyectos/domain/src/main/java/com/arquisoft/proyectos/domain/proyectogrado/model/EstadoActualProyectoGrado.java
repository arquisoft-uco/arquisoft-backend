package com.arquisoft.proyectos.domain.proyectogrado.model;

import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGrado;

import java.util.UUID;

public record EstadoActualProyectoGrado(UUID proyectoGrado, EstadoProyectoGrado estadoActual) {}
