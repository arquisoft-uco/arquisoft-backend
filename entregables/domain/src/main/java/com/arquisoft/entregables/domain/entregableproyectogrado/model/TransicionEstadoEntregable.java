package com.arquisoft.entregables.domain.entregableproyectogrado.model;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;

import java.util.UUID;

public record TransicionEstadoEntregable(UUID entregableProyectoGrado, EstadoEntregable actual, EstadoEntregable nuevo) {}
