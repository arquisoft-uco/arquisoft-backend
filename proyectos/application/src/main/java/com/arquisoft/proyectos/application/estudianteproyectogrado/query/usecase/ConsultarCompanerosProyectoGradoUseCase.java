package com.arquisoft.proyectos.application.estudianteproyectogrado.query.usecase;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.CompaneroProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.CompaneroProyectoGradoReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarCompanerosProyectoGradoUseCase
        extends UseCase<CompaneroProyectoGradoCriteria, List<CompaneroProyectoGradoReadModel>> {}
