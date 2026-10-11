package com.arquisoft.proyectos.application.estudianteproyectogrado.query.secondaryport;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteProyectoGradoReadModel;

import java.util.List;
import java.util.UUID;

public interface EstudianteProyectoGradoQueryOutputPort {

    List<EstudianteProyectoGradoReadModel> consultarPorProyecto(UUID proyectoGrado);
}
