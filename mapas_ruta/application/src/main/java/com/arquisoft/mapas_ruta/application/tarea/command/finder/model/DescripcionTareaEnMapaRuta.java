package com.arquisoft.mapas_ruta.application.tarea.command.finder.model;

import java.util.UUID;

public record DescripcionTareaEnMapaRuta(UUID tarea, UUID mapaRuta, String descripcion) {}
