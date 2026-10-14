package com.arquisoft.proyectos.application.asesorproyectogrado.query.usecase;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorProyectoGradoReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarAsesoresProyectoGradoEstudianteUseCase
        extends UseCase<AsesorProyectoGradoEstudianteCriteria, List<AsesorProyectoGradoReadModel>> {}
