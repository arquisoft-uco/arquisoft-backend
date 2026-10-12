package com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.interactor;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model.ConsultarAsesoresProyectoGradoEstudianteQuery;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorProyectoGradoReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarAsesoresProyectoGradoEstudianteInteractor
        extends Interactor<ConsultarAsesoresProyectoGradoEstudianteQuery, List<AsesorProyectoGradoReadModel>> {}
