package com.arquisoft.mapas_ruta.domain.tarea.model;

import java.util.UUID;

public record DisponibilidadTarea(UUID mapaRuta, String descripcion, boolean existe) {}
