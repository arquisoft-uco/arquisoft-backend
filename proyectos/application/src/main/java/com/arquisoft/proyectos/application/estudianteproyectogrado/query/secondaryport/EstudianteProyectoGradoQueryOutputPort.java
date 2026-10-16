package com.arquisoft.proyectos.application.estudianteproyectogrado.query.secondaryport;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteAsesoradoProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.CompaneroProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteProyectoGradoReadModel;

import java.util.List;
import java.util.UUID;

public interface EstudianteProyectoGradoQueryOutputPort {

    List<EstudianteProyectoGradoReadModel> consultarPorProyecto(UUID proyectoGrado);

    List<CompaneroProyectoGradoReadModel> consultarCompanerosVigentesPorProyecto(UUID proyectoGrado);

    List<EstudianteAsesoradoProyectoGradoReadModel> consultarAsesoradosVigentesPorProyecto(UUID proyectoGrado);
}
