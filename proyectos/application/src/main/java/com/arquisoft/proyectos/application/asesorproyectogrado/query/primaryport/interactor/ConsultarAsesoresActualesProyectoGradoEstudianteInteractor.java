package com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.interactor;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model.ConsultarAsesoresActualesProyectoGradoEstudianteQuery;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorActualProyectoGradoReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarAsesoresActualesProyectoGradoEstudianteInteractor
        extends Interactor<ConsultarAsesoresActualesProyectoGradoEstudianteQuery,
        List<AsesorActualProyectoGradoReadModel>> {}
