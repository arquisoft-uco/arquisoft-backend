package com.arquisoft.proyectos.domain.proyectogrado.model;

import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;

import java.util.UUID;

public record EstadoUltimoEntregable(UUID proyectoGrado, String tituloProyecto, int version,
                                     EstadoEntregable estadoEntregable) {}
