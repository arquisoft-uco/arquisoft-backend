package com.arquisoft.proyectos.application.estadoproyectogrado.query.secondaryport;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.readmodel.EstadoProyectoGradoReadModel;

import java.util.List;
import java.util.UUID;

public interface EstadoProyectoGradoQueryOutputPort {

    List<EstadoProyectoGradoReadModel> consultarPorProyecto(UUID proyectoGrado);
}
