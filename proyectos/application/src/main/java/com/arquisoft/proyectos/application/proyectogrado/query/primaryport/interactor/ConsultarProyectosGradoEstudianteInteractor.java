package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor;

import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarProyectosGradoEstudianteQuery;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarProyectosGradoEstudianteInteractor
        extends Interactor<ConsultarProyectosGradoEstudianteQuery, List<ProyectoGradoReadModel>> {}
