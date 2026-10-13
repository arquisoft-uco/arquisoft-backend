package com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.interactor;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model.ConsultarEstudiantesAsesoradosProyectoGradoQuery;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteAsesoradoProyectoGradoReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarEstudiantesAsesoradosProyectoGradoInteractor extends
        Interactor<ConsultarEstudiantesAsesoradosProyectoGradoQuery, List<EstudianteAsesoradoProyectoGradoReadModel>> {}
