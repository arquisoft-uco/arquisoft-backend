package com.arquisoft.proyectos.domain.asesorproyectogrado.model;

import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;

import java.util.UUID;

public record TransicionEstadoAsesor(UUID asesor, EstadoAsesor actual, EstadoAsesor nuevo) {}
