package com.arquisoft.entregables.domain.paqueteentregable.model;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;

import java.util.UUID;

public record EstadoEntregableDelPaquete(UUID entregableProyectoGrado, int version, EstadoEntregable estadoEntregable) {}
