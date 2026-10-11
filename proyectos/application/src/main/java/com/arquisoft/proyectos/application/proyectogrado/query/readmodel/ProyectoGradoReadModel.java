package com.arquisoft.proyectos.application.proyectogrado.query.readmodel;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.readmodel.EstadoProyectoGradoReadModel;

import java.util.UUID;

public record ProyectoGradoReadModel(
        UUID id,
        UUID fichaPerfil,
        String tituloProyecto,
        EstadoProyectoGradoReadModel estado
) {}
