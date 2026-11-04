package com.arquisoft.proyectos.application.asesorproyectogrado.query.usecase;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorProyectoGradoCoordinadorCriteria;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorProyectoGradoReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarAsesoresProyectoGradoCoordinadorUseCase
        extends UseCase<AsesorProyectoGradoCoordinadorCriteria, List<AsesorProyectoGradoReadModel>> {}
