package com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.entity;

import java.util.UUID;

public record TareaEntity(UUID id, UUID mapaRuta, String descripcion, UUID asesor, String estadoItem) {}
