package com.arquisoft.proyectos.application.proyectogrado.query.readmodel;

import com.arquisoft.proyectos.application.estadoproyecto.query.readmodel.EstadoProyectoReadModel;

import java.util.UUID;

public record ProyectoGradoReadModel(
        UUID id,
        UUID fichaPerfil,
        String tituloProyecto,
        EstadoProyectoReadModel estado
) {}
