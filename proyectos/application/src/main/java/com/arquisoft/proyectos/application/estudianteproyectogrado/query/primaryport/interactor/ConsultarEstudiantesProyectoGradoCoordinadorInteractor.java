package com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.interactor;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model.ConsultarEstudiantesProyectoGradoCoordinadorQuery;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteProyectoGradoReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarEstudiantesProyectoGradoCoordinadorInteractor
        extends Interactor<ConsultarEstudiantesProyectoGradoCoordinadorQuery, List<EstudianteProyectoGradoReadModel>> {}
