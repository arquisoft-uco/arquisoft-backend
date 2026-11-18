package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor;

import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarCoordinadoresProyectoGradoEstudianteQuery;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.CoordinadorProyectoGradoReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarCoordinadoresProyectoGradoEstudianteInteractor
        extends Interactor<ConsultarCoordinadoresProyectoGradoEstudianteQuery,
        List<CoordinadorProyectoGradoReadModel>> {}
