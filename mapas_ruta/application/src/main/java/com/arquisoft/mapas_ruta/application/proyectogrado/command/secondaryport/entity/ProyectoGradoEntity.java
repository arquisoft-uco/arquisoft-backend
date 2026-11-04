package com.arquisoft.mapas_ruta.application.proyectogrado.command.secondaryport.entity;

import java.util.UUID;

public record ProyectoGradoEntity(
        UUID id,
        String estadoProyectoGrado,
        UUID coordinador,
        UUID fichaPerfil,
        String tituloProyecto
) {}
