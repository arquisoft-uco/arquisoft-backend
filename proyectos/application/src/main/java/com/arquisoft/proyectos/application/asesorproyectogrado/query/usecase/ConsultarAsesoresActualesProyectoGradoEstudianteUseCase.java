package com.arquisoft.proyectos.application.asesorproyectogrado.query.usecase;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorActualProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorActualProyectoGradoReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarAsesoresActualesProyectoGradoEstudianteUseCase
        extends UseCase<AsesorActualProyectoGradoEstudianteCriteria, List<AsesorActualProyectoGradoReadModel>> {}
