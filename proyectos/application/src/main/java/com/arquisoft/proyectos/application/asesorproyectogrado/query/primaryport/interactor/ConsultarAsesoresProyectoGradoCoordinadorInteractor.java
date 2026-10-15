package com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.interactor;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model.ConsultarAsesoresProyectoGradoCoordinadorQuery;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorProyectoGradoReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarAsesoresProyectoGradoCoordinadorInteractor
        extends Interactor<ConsultarAsesoresProyectoGradoCoordinadorQuery, List<AsesorProyectoGradoReadModel>> {}
