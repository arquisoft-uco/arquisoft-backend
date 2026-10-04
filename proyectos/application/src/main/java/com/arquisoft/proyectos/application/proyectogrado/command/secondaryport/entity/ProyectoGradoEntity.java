package com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.entity;

import java.util.UUID;

public record ProyectoGradoEntity(UUID id, UUID fichaPerfil, String tituloProyecto, UUID coordinador,
                                  String estadoProyectoGrado) {
}
